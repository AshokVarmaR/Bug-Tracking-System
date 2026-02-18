package com.bts.services;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bts.dtos.BugCreateRequestDTO;
import com.bts.dtos.BugResponseDTO;
import com.bts.enums.BugStatus;
import com.bts.enums.NotificationType;
import com.bts.enums.ReferenceType;
import com.bts.exceptions.ResourceNotFoundException;
import com.bts.mappers.BugMapper;
import com.bts.models.Attachment;
import com.bts.models.Bug;
import com.bts.models.Project;
import com.bts.models.User;
import com.bts.repositories.AttachmentRepository;
import com.bts.repositories.BugRepository;
import com.bts.repositories.ProjectRepository;
import com.bts.repositories.UserRepository;
import com.bts.utils.FileManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BugService {

    private final BugRepository bugRepo;
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
    private final AttachmentRepository attachmentRepo;
    private final BugMapper bugMapper;
    private final FileManager fileManager;
    private final NotificationService notificationService;

    /* =====================
       CREATE BUG (Tester)
       ===================== */
    public BugResponseDTO createBug(BugCreateRequestDTO request) {

        User tester = getCurrentUser();

        Project project = projectRepo.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Bug bug = new Bug();
        bug.setTitle(request.getTitle());
        bug.setDescription(request.getDescription());
        bug.setSeverity(request.getSeverity());
        bug.setPriority(request.getPriority());
        bug.setStatus(BugStatus.OPEN);
        bug.setProject(project);
        bug.setCreatedBy(tester);
        
        return bugMapper.toResponse(bugRepo.save(bug));
    }

    /* =====================
       ASSIGN BUG
       ===================== */
    public BugResponseDTO assignBug(Long bugId, Long developerId) {

        Bug bug = getBug(bugId);

        User developer = userRepo.findById(developerId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found"));

        bug.setAssignedTo(developer);
        bug.setStatus(BugStatus.ASSIGNED);
        
        notificationService.createNotification(new ArrayList(List.of(developer)), bug.getCreatedBy(), NotificationType.BUG_STATUS, ReferenceType.BUG, bug.getId(), "Bug Assinged to you");

        return bugMapper.toResponse(bugRepo.save(bug));
    }

    /* =====================
       UPDATE STATUS
       ===================== */
    public BugResponseDTO updateStatus(Long bugId, BugStatus status, String remarks) {

        Bug bug = getBug(bugId);
        User currentUser = getCurrentUser();

        BugStatus oldStatus = bug.getStatus();
        bug.setStatus(status);

        if (remarks != null && !remarks.isBlank()) {
            String previousRemarks = bug.getRemarks() != null ? bug.getRemarks() : "";
            bug.setRemarks(
                previousRemarks +
                "\n" + currentUser.getName() +
                "(" + currentUser.getRole() + ") : " + remarks
            );
        }


        if (oldStatus != status) {
            User recipient = getOtherParty(bug, currentUser);

            if (recipient != null) {
                notificationService.createNotification(
                    List.of(recipient),
                    currentUser,
                    NotificationType.BUG_STATUS,
                    ReferenceType.BUG,
                    bug.getProject().getId(),
                    "Bug #" + bug.getId() +
                    " status changed to " + status
                );
            }
        }

        return bugMapper.toResponse(bugRepo.save(bug));
    }


    /* =====================
       ADD ATTACHMENT
       ===================== */
    public void addAttachment(Long bugId, MultipartFile file) {

        Bug bug = getBug(bugId);

        String fileName =
            "bug_" + bugId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        String url = fileManager.save(file, fileName);

        Attachment attachment = new Attachment();
        attachment.setBug(bug);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFilePath(url);
        attachment.setFileSize(file.getSize());
        attachment.setFileType(file.getContentType());
        attachment.setUploadedBy(getCurrentUser());

        attachmentRepo.save(attachment);
    }

    /* =====================
       FETCH BUGS
       ===================== */
    public List<BugResponseDTO> getBugsByProject(Long projectId) {

        return bugRepo.findByProjectId(projectId)
                .stream()
                .map(bugMapper::toResponse)
                .toList();
    }

    /* =====================
       HELPERS
       ===================== */
    private Bug getBug(Long bugId) {
        return bugRepo.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug not found"));
    }

    private User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Hiiiiiiiiiii");
        System.out.println(user.getEmail());
        
        return userRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

	public List<BugResponseDTO> getAllProjectBugsAssignedToDeveloper(Long userId, Long projectId) {
		// TODO Auto-generated method stub
		
		return bugMapper.toResponseList(bugRepo.findByAssignedTo_IdAndProject_Id(userId, projectId));
	}

	public List<BugResponseDTO> fetchAllProjectBugsCreatedByTester(Long userId, Long projectId) {
		// TODO Auto-generated method stub
		return bugMapper.toResponseList(bugRepo.findByCreatedBy_IdAndProject_Id(userId, projectId));
	}
	
	private User getOtherParty(Bug bug, User currentUser) {

	    if (currentUser.getId().equals(bug.getCreatedBy().getId())) {
	        // Tester acted → notify Developer
	        return bug.getAssignedTo();
	    }

	    // Developer acted → notify Tester
	    return bug.getCreatedBy();
	}

	public List<BugResponseDTO> fetchAllBugs() {
		// TODO Auto-generated method stub
		return bugMapper.toResponseList(bugRepo.findAll());
	}

}
