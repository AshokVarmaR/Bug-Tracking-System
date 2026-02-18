package com.bts.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bts.dtos.ProjectAssignmentResponseDTO;
import com.bts.services.ProjectAssignmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/project-assignments")
@RequiredArgsConstructor
public class ProjectAssignmentController {

    private final ProjectAssignmentService assignmentService;

    // 🔹 Get current assignment of a user
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<ProjectAssignmentResponseDTO> getCurrentAssignment(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
            assignmentService.getCurrentAssignmentByUser(userId)
        );
    }
    
    @GetMapping("/user/{userId}/projects")
    public ResponseEntity<List<ProjectAssignmentResponseDTO>> getAllAssignmentsOfUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
            assignmentService.getAllAssignmentsOfUser(userId)
        );
    }

    // 🔹 Get all active assignments for a project
    @GetMapping("/project/{projectId}/active")
    public ResponseEntity<List<ProjectAssignmentResponseDTO>> getActiveByProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
            assignmentService.getActiveAssignmentsByProject(projectId)
        );
    }

    // 🔹 Unassign user from current project
    @PutMapping("/unassign/{userId}")
    public ResponseEntity<Void> unassignUser(
            @PathVariable Long userId
    ) {
    	System.out.println("called");
        assignmentService.unassignUser(userId);
        return ResponseEntity.ok().build();
    }
}
