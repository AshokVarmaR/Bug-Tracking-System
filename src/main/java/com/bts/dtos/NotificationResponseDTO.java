package com.bts.dtos;

import com.bts.enums.NotificationType;
import com.bts.enums.ReferenceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponseDTO {

    private Long id;

    private String message;

    private boolean read;

    private LocalDateTime createdAt;

    private NotificationType type;

    private ReferenceType referenceType;

    private Long referenceId;

    // Who triggered the notification
    private UserResponseDTO sender;
}
 