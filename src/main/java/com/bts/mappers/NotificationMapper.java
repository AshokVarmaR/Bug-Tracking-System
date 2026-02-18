package com.bts.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.bts.dtos.NotificationResponseDTO;
import com.bts.models.Notification;

@Mapper(
	    componentModel = "spring",
	    uses = { UserMapper.class },
	    unmappedTargetPolicy = ReportingPolicy.IGNORE
	)
	public interface NotificationMapper {

//	    @Mapping(source = "actor", target = "sender")
	    NotificationResponseDTO toResponseDTO(Notification notification);
	}