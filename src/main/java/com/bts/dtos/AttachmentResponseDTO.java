package com.bts.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttachmentResponseDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String filePath;
    private UserResponseDTO uploadedBy;
    private LocalDateTime uploadedAt;
}
