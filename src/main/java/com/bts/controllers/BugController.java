package com.bts.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bts.dtos.*;
import com.bts.enums.*;
import com.bts.services.BugService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;
    
    @GetMapping
    public ResponseEntity<List<BugResponseDTO>> allBugs(){
    	return ResponseEntity.ok(
    			bugService.fetchAllBugs()
    			);
    }
    
    
    @GetMapping("/developer/assigned/{userId}/{projectId}")
    ResponseEntity<List<BugResponseDTO>> fetchAllProjectBugsAssignedToDeveloper(
    		@PathVariable Long userId, @PathVariable Long projectId){
        return ResponseEntity.ok(
                bugService.getAllProjectBugsAssignedToDeveloper(userId,projectId)
            );
    }
    
    @GetMapping("/tester/assigned/{userId}/{projectId}")
    ResponseEntity<List<BugResponseDTO>> fetchAllProjectBugsCreatedByTester(
    		@PathVariable Long userId, @PathVariable Long projectId){
        return ResponseEntity.ok(
                bugService.fetchAllProjectBugsCreatedByTester(userId,projectId)
            );
    }

    // 🔹 Create bug (Tester)
    @PostMapping
    public ResponseEntity<BugResponseDTO> createBug(
            @RequestBody BugCreateRequestDTO request
    ) {
        return ResponseEntity.ok(bugService.createBug(request));
    }

    // 🔹 Assign bug to developer
    @PutMapping("/{bugId}/assign")
    public ResponseEntity<BugResponseDTO> assignBug(
            @PathVariable Long bugId,
            @RequestBody BugAssignRequestDTO request
    ) {
        return ResponseEntity.ok(
            bugService.assignBug(bugId, request.getDeveloperId())
        );
    }

    // 🔹 Update bug status
    @PutMapping("/{bugId}/status")
    public ResponseEntity<BugResponseDTO> updateStatus(
            @PathVariable Long bugId,
            @RequestBody BugStatusUpdateDTO request
    ) {
        return ResponseEntity.ok(
            bugService.updateStatus(bugId, request.getStatus(), request.getRemarks())
        );
    }

    // 🔹 Upload attachment
    @PostMapping("/{bugId}/attachments")
    public ResponseEntity<Void> uploadAttachment(
            @PathVariable Long bugId,
            @RequestParam MultipartFile file
    ) {
        bugService.addAttachment(bugId, file);
        return ResponseEntity.ok().build();
    }

    // 🔹 Fetch bugs by project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<BugResponseDTO>> getByProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
            bugService.getBugsByProject(projectId)
        );
    }

    // 🔹 Fetch enums for UI
    @GetMapping("/statuses")
    public BugStatus[] getStatuses() {
        return BugStatus.values();
    }

    @GetMapping("/priorities")
    public Priority[] getPriorities() {
        return Priority.values();
    }

    @GetMapping("/severities")
    public Severity[] getSeverities() {
        return Severity.values();
    }
}
