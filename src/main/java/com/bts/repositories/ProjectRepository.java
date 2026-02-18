package com.bts.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bts.models.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	List<Project> findByTesters_Id(Long id);

	List<Project> findByDevelopers_Id(Long id);

	List<Project> findByProjectManager_Id(Long userId);

}
