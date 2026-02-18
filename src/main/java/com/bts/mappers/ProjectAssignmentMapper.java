package com.bts.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.bts.dtos.ProjectAssignmentResponseDTO;
import com.bts.models.ProjectAssignment;

@Mapper(config = MapStructConfig.class)
public interface ProjectAssignmentMapper {

	ProjectAssignmentResponseDTO toResponse(ProjectAssignment assignment);
	List<ProjectAssignmentResponseDTO> toResponseList(List<ProjectAssignment> assignment);
}
