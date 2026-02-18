package com.bts.dtos;

import java.time.LocalDateTime;

import com.bts.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserResponseDTO {
    private Long id;
    private String name;
    private String employeeId;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
