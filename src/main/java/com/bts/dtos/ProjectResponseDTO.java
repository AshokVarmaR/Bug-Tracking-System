package com.bts.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.bts.enums.ProjectStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String remarks;
    private String document;
    private LocalDate dueDate;
    private ProjectStatus status;
    private UserResponseDTO projectManager;
    private List<UserResponseDTO> developers;
    private List<UserResponseDTO> testers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
