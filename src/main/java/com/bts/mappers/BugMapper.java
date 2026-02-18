package com.bts.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bts.dtos.BugResponseDTO;
import com.bts.models.Bug;

@Mapper(config = MapStructConfig.class, uses = {AttachmentMapper.class})
public interface BugMapper {

    @Mapping(source = "project", target = "project")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "assignedTo", target = "assignedTo")
    BugResponseDTO toResponse(Bug bug);
    
    List<BugResponseDTO> toResponseList(List<Bug> bugs);
}
