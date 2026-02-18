package com.bts.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bts.models.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {

	List<Bug> findByProjectId(Long projectId);

	List<Bug> findByAssignedTo_IdAndProject_Id(Long userId, Long projectId);

	List<Bug> findByCreatedBy_IdAndProject_Id(Long userId, Long projectId);

}
