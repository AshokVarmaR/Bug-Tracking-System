package com.bts.services;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.bts.dtos.ProjectAssignmentResponseDTO;
import com.bts.mappers.ProjectAssignmentMapper;
import com.bts.models.ProjectAssignment;
import com.bts.repositories.ProjectAssignmentRepository;
import com.bts.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectAssignmentService {

    private final ProjectAssignmentRepository assignmentRepo;
    private final ProjectAssignmentMapper assignmentMapper;

    // 🔹 Get user's current working project
    public ProjectAssignmentResponseDTO getCurrentAssignmentByUser(Long userId) {
        ProjectAssignment assignment =
            assignmentRepo.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User has no active project assignment")
                );

        return assignmentMapper.toResponse(assignment);
    }

    // 🔹 Get all active assignments for a project
    public List<ProjectAssignmentResponseDTO> getActiveAssignmentsByProject(Long projectId) {
        return assignmentRepo.findByProjectIdAndActiveTrue(projectId)
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();
    }

    // 🔹 Unassign user from project
    public void unassignUser(Long userId) {
        ProjectAssignment assignment =
            assignmentRepo.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Active assignment not found")
                );

        assignment.setActive(false);
        assignmentRepo.save(assignment);
    }

	public List<ProjectAssignmentResponseDTO> getAllAssignmentsOfUser(Long userId) {
		// TODO Auto-generated method stub
		return assignmentMapper.toResponseList(assignmentRepo.findByUser_Id(userId));
	}
}
