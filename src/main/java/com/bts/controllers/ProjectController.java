package com.bts.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bts.dtos.AssignUsersRequestDTO;
import com.bts.dtos.ProjectRequestDTO;
import com.bts.dtos.ProjectResponseDTO;
import com.bts.enums.ProjectStatus;
import com.bts.services.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    
    @GetMapping("/statuses")
    public ProjectStatus[] projectrStatuses() {
    	return ProjectStatus.values();
    }
    
    @GetMapping("/manager/{userId}")
    public ResponseEntity<List<ProjectResponseDTO>> managerProjects(@PathVariable Long userId) {
        return ResponseEntity.ok(projectService.getProjectsOfProjectManager(userId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @RequestPart("project") ProjectRequestDTO dto,
            @RequestPart(value = "document", required = false) MultipartFile document
    ) {
        return ResponseEntity.ok(projectService.createProject(dto, document));
    }

    @PostMapping("/{projectId}/assign")
    public ResponseEntity<ProjectResponseDTO> assignUsers(
            @PathVariable Long projectId,
            @RequestBody AssignUsersRequestDTO request
    ) {
        return ResponseEntity.ok(
                projectService.assignUsers(projectId, request.getDevelopers(), request.getTesters())
        );
    }

    @PutMapping("/{projectId}/unassign")
    public ResponseEntity<ProjectResponseDTO> unassignDeveloperOrTester(
            @PathVariable Long projectId,
            @RequestParam Long userId // developer or tester
    ) {
        return ResponseEntity.ok(
                projectService.unassignDeveloperOrTester(projectId, userId)
        );
    }
    
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProject(projectId));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/developer")
    public ResponseEntity<List<ProjectResponseDTO>> developerProjects() {
        return ResponseEntity.ok(projectService.getProjectsForDeveloper());
    }

    @GetMapping("/tester")
    public ResponseEntity<List<ProjectResponseDTO>> testerProjects() {
        return ResponseEntity.ok(projectService.getProjectsForTester());
    }
    
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long projectId,
            @RequestPart("project") ProjectRequestDTO dto,
            @RequestPart(value = "document", required = false) MultipartFile document
    ) {
        return ResponseEntity.ok(
                projectService.updateProject(projectId, dto, document)
        );
    }
    
    @PutMapping("/{projectId}/status")
    public ResponseEntity<ProjectResponseDTO> updateStatus(
            @PathVariable Long projectId,
            @RequestParam ProjectStatus status,
            @RequestParam(required = false) String remarks
    ) {
        return ResponseEntity.ok(projectService.updateStatus(projectId, status, remarks));
    }
    
    

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully");
    }
}
