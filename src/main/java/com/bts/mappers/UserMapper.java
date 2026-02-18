package com.bts.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bts.dtos.ProjectResponseDTO;
import com.bts.dtos.UserCreateRequestDTO;
import com.bts.dtos.UserResponseDTO;
import com.bts.models.Project;
import com.bts.models.User;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(UserCreateRequestDTO dto);

    UserResponseDTO toResponse(User user);
    List<UserResponseDTO> toResponseList(List<User> users);
}

