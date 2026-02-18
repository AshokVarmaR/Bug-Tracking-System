package com.bts.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.bts.enums.BugStatus;
import com.bts.enums.Priority;
import com.bts.enums.Severity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BugResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String remarks;
    private Severity severity;
    private Priority priority;
    private BugStatus status;
    private List<AttachmentResponseDTO> attachments;

    private ProjectResponseDTO project;
    
    private UserResponseDTO createdBy;
    private UserResponseDTO assignedTo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
