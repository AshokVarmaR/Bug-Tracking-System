package com.bts.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bts.dtos.ProjectRequestDTO;
import com.bts.dtos.ProjectResponseDTO;
import com.bts.models.Project;

@Mapper(config = MapStructConfig.class)
public interface ProjectMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "projectManager", ignore = true)
	@Mapping(target = "developers", ignore = true)
	@Mapping(target = "testers", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "document", ignore = true)
	@Mapping(target = "remarks", ignore = true)
    Project toEntity(ProjectRequestDTO dto); 

    ProjectResponseDTO toResponse(Project project);
    List<ProjectResponseDTO> toResponseList(List<Project> project);
}