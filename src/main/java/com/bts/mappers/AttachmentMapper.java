package com.bts.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bts.dtos.AttachmentResponseDTO;
import com.bts.models.Attachment;

@Mapper(config = MapStructConfig.class)
public interface AttachmentMapper {

	@Mapping(target="uploadedAt", source="createdAt")
	AttachmentResponseDTO toResponse(Attachment att);
	
	List<AttachmentResponseDTO> toResponseList(List<Attachment> atts);
}
