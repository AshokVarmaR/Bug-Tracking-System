package com.bts.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentResponseDTO {
    private Long id;
    private String message;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
