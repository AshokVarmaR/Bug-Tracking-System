package com.bts.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bts.dtos.ProjectRequestDTO;
import com.bts.dtos.ProjectResponseDTO;
import com.bts.enums.NotificationType;
import com.bts.enums.ProjectStatus;
import com.bts.enums.ReferenceType;
import com.bts.enums.Role;
import com.bts.exceptions.ResourceNotFoundException;
import com.bts.mappers.ProjectMapper;
import com.bts.models.Project;
import com.bts.models.ProjectAssignment;
import com.bts.models.User;
import com.bts.repositories.ProjectAssignmentRepository;
import com.bts.repositories.ProjectRepository;
import com.bts.repositories.UserRepository;
import com.bts.utils.FileManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
    private final ProjectAssignmentRepository assignmentRepo;
    
    private final ProjectMapper projectMapper;
    private final FileManager fileManager;
    
    private final NotificationService notificationService;

    /* =========================
       CREATE PROJECT
       ========================= */
    public ProjectResponseDTO createProject(ProjectRequestDTO dto, MultipartFile document) {

        User projectManager = getCurrentUser();

        if (projectManager.getRole() != Role.PROJECT_MANAGER) {
            throw new ResourceNotFoundException("Only Project Manager can create projects");
        }

        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setDueDate(dto.getDueDate());
        project.setProjectManager(projectManager);
        project.setStatus(ProjectStatus.CREATED);

        if (document != null && !document.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + document.getOriginalFilename();
            String filePath = fileManager.save(document, fileName);
            project.setDocument(filePath);
        }

        return projectMapper.toResponse(projectRepo.save(project));
    }

    /* =========================
       ASSIGN DEVELOPERS & TESTERS
       ========================= */
    @Transactional
    public ProjectResponseDTO assignUsers(
            Long projectId,
            List<Long> developerIds,
            List<Long> testerIds
    ) {
        Project project = getProjectEntity(projectId);
        User sender = getCurrentUser();

        Set<User> developers = new HashSet<>(userRepo.findAllById(developerIds));
        Set<User> testers = new HashSet<>(userRepo.findAllById(testerIds));

        // 1️⃣ Assign via ProjectAssignment
        assign(project, developers, Role.DEVELOPER);
        assign(project, testers, Role.TESTER);

        // 2️⃣ Add to collections
        project.getDevelopers().addAll(developers);
        project.getTesters().addAll(testers);

        // 3️⃣ Status transition
        if (project.getStatus() == ProjectStatus.CREATED) {
            project.setStatus(ProjectStatus.IN_DEVELOPMENT);
        }

        // 4️⃣ Notifications
        String message = "You have been assigned to project: " + project.getName();

        notificationService.createNotification(
            new ArrayList<>(developers),
            sender,
            NotificationType.ASSIGNMENT,
            ReferenceType.PROJECT,
            project.getId(),
            message
        );

        notificationService.createNotification(
            new ArrayList<>(testers),
            sender,
            NotificationType.ASSIGNMENT,
            ReferenceType.PROJECT,
            project.getId(),
            message
        );

        return projectMapper.toResponse(projectRepo.save(project));
    }


    
    private void assign(Project project, Set<User> users, Role role) {
        for (User user : users) {

            assignmentRepo.findByUserAndActiveTrue(user)
                .ifPresentOrElse(existing -> {

                    // ✅ Already assigned to same project → skip
                    if (existing.getProject().getId().equals(project.getId())) {
                        return;
                    }

                    // 🔁 Assigned to different project → reassign
                    existing.setActive(false);
                    existing.setEndDate(LocalDate.now());
                    assignmentRepo.save(existing);

                    assignmentRepo.flush(); // important

                    createNewAssignment(project, user, role);

                }, () -> {
                    // 🆕 No active assignment → assign
                    createNewAssignment(project, user, role);
                });
        }
    }
    private void createNewAssignment(Project project, User user, Role role) {
        ProjectAssignment pa = new ProjectAssignment();
        pa.setUser(user);
        pa.setProject(project);
        pa.setRole(role);
        pa.setActive(true);

        assignmentRepo.save(pa);
    }



    private void validateNoActiveAssignment(List<User> users) {
        for (User user : users) {
            if (assignmentRepo.existsByUserIdAndActiveTrue(user.getId())) {
                throw new IllegalStateException(
                    "User " + user.getEmail() + " is already assigned to a project"
                );
            }
        }
    }
    
    
    private void createAssignments(
            Project project,
            List<User> users,
            Role role
    ) {
        for (User user : users) {
            ProjectAssignment assignment = new ProjectAssignment();
            assignment.setUser(user);
            assignment.setProject(project);
            assignment.setRole(role);
            assignment.setActive(true);

            assignmentRepo.save(assignment);
        }
    }

    
    
    /* =========================
       GET PROJECT
       ========================= */
    public ProjectResponseDTO getProject(Long projectId) {
        return projectMapper.toResponse(getProjectEntity(projectId));
    }

    /* =========================
       GET ALL PROJECTS
       ========================= */
    public List<ProjectResponseDTO> getAllProjects() {
        return projectMapper.toResponseList(projectRepo.findAll());
    }

    /* =========================
       DEVELOPER PROJECTS
       ========================= */
    public List<ProjectResponseDTO> getProjectsForDeveloper() {
        User developer = getCurrentUser();
        return projectMapper.toResponseList(
                projectRepo.findByDevelopers_Id(developer.getId())
        );
    }

    /* =========================
       TESTER PROJECTS
       ========================= */
    public List<ProjectResponseDTO> getProjectsForTester() {
        User tester = getCurrentUser();
        return projectMapper.toResponseList(
                projectRepo.findByTesters_Id(tester.getId())
        );
    }

    /* =========================
       UPDATE PROJECT STATUS
       ========================= */
    @Transactional
    public ProjectResponseDTO updateStatus(Long projectId, ProjectStatus status, String remarks) {

        Project project = getProjectEntity(projectId);
        User user = getCurrentUser();

        project.setStatus(status);

        if (remarks != null && !remarks.isBlank()) {
            String previousRemarks = project.getRemarks() != null
                    ? project.getRemarks()
                    : "";

            project.setRemarks(
                previousRemarks +
                "\n" + user.getName() + "(" + user.getRole() + ") : " + remarks
            );
        }

        // ✅ Deactivate assignments if project completed
        if (status == ProjectStatus.COMPLETED) {
            List<ProjectAssignment> activeAssignments =
                    assignmentRepo.findByProjectIdAndActiveTrue(projectId);

            activeAssignments.forEach(a -> {
                a.setActive(false);
                a.setEndDate(LocalDate.now());
            });
        }

        // 🔔 Notifications
        Set<User> recipients = new HashSet<>();
        recipients.addAll(project.getDevelopers());
        recipients.addAll(project.getTesters());
        recipients.remove(user); // don’t notify actor

        if (!recipients.isEmpty()) {
            String message = "Project '" + project.getName() +
                    "' status changed to " + status;

            notificationService.createNotification(
                new ArrayList<>(recipients),
                user,
                NotificationType.PROJECT_STATUS,
                ReferenceType.PROJECT,
                project.getId(),
                message
            );
        }

        return projectMapper.toResponse(projectRepo.save(project));
    }



    /* =========================
       DELETE PROJECT
       ========================= */
    public void deleteProject(Long projectId) {
        Project project = getProjectEntity(projectId);

        if (project.getDocument() != null) {
            fileManager.delete(project.getDocument().replace("/", ""));
        }

        projectRepo.delete(project);
    }

    /* =========================
       HELPERS
       ========================= */
    private Project getProjectEntity(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    private User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Hiiiiiiiiiii");
        System.out.println(user.getEmail());
        
        return userRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public ProjectResponseDTO unassignDeveloperOrTester(
            Long projectId,
            Long userId
    ) {

        Project project = getProjectEntity(projectId);

        ProjectAssignment assignment =
            assignmentRepo.findByUserIdAndProjectIdAndActiveTrue(userId, projectId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Active assignment not found")
                );

        // Remove from project-side lists (UI convenience)
        if (assignment.getRole() == Role.DEVELOPER) {
            project.getDevelopers().remove(assignment.getUser());
        } else if (assignment.getRole() == Role.TESTER) {
            project.getTesters().remove(assignment.getUser());
        }

        // Deactivate assignment (SOURCE OF TRUTH)
        assignment.setActive(false);
        assignment.setEndDate(LocalDate.now());

        assignmentRepo.save(assignment);

        return projectMapper.toResponse(projectRepo.save(project));
    }


	
    @Transactional
    public ProjectResponseDTO updateProject(
            Long projectId,
            ProjectRequestDTO dto,
            MultipartFile document
    ) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = getCurrentUser();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            project.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            project.setDescription(dto.getDescription());
        }

        if (dto.getDueDate() != null) {
            project.setDueDate(dto.getDueDate());
        }

        if (document != null && !document.isEmpty()) {
            fileManager.replace(document, project.getDocument());
        }

        Project saved = projectRepo.save(project);

        // 🔔 Notify assignees
        Set<User> recipients = new HashSet<>();
        recipients.addAll(project.getDevelopers());
        recipients.addAll(project.getTesters());
        recipients.remove(user);

        if (!recipients.isEmpty()) {
            notificationService.createNotification(
                new ArrayList<>(recipients),
                user,
                NotificationType.PROJECT_UPDATE,
                ReferenceType.PROJECT,
                project.getId(),
                "Project details updated"
            );
        }

        return projectMapper.toResponse(saved);
    }


	public List<ProjectResponseDTO> getProjectsOfProjectManager(Long userId) {
		// TODO Auto-generated method stub
		return projectMapper.toResponseList(
				projectRepo.findByProjectManager_Id(userId)
				);
	}

}
