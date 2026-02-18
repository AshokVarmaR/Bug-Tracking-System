package com.bts.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bts.models.Project;
import com.bts.models.ProjectAssignment;
import com.bts.models.User;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Long> {

	List<ProjectAssignment> findByUser(User user);
	List<ProjectAssignment> findByUser_Id(Long userId);
	boolean existsByUserIdAndActiveTrue(Long id);
	List<ProjectAssignment> findByProjectIdAndActiveTrue(Long projectId);
	Optional<ProjectAssignment> findByUserIdAndProjectIdAndActiveTrue(Long userId, Long projectId);
	Optional<ProjectAssignment> findByUserAndActiveTrue(User user);
	Optional<ProjectAssignment> findByUserIdAndActiveTrue(Long userId);
	List<ProjectAssignment> findByProjectId(Long projectId);

}
