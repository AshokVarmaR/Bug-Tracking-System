package com.bts.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bts.dtos.NotificationResponseDTO;
import com.bts.enums.NotificationType;
import com.bts.enums.ReferenceType;
import com.bts.mappers.NotificationMapper;
import com.bts.mappers.UserMapper;
import com.bts.models.Notification;
import com.bts.models.User;
import com.bts.repositories.NotificationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final UserService userService;
    private final NotificationMapper notificationMapper;

    /**
     * Create notifications for multiple recipients.
     *
     * @param recipients   users who should receive notification
     * @param actor        user who triggered the action
     * @param type         PROJECT_STATUS / BUG_STATUS / ASSIGNMENT
     * @param referenceType PROJECT / BUG
     * @param referenceId  projectId or bugId
     * @param message      notification message
     */
    
    @Transactional
    public void createNotification(
            List<User> recipients,
            User actor,
            NotificationType type,
            ReferenceType referenceType,
            Long referenceId,
            String message
    ) {
        recipients.stream()
                .filter(user -> actor == null || !user.getId().equals(actor.getId()))
                .forEach(user -> {

                    Notification notification = new Notification();
                    notification.setRecipient(user);
                    notification.setActor(actor);
                    notification.setType(type);
                    notification.setReferenceType(referenceType);
                    notification.setReferenceId(referenceId);
                    notification.setMessage(message);
                    notification.setRead(false);

                    notificationRepo.save(notification);
                });
    }
    
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getAllNotifications() {
        User currentUser = userService.getCurrentUser();

        return notificationRepo
                .findByRecipientIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponseDTO)
                .toList();
    }

    // 🔹 Fetch only unread notifications
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications() {
        User currentUser = userService.getCurrentUser();

        return notificationRepo
                .findByRecipientIdAndReadFalseOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponseDTO)
                .toList();
    }

    // 🔹 Get unread count
    @Transactional(readOnly = true)
    public Long getUnreadCount() {
        User currentUser = userService.getCurrentUser();
        return notificationRepo.countByRecipientIdAndReadFalse(currentUser.getId());
    }

    // 🔹 Mark single notification as read
    public void markAsRead(Long notificationId) {
        User currentUser = userService.getCurrentUser();

        Notification notification = notificationRepo
                .findByIdAndRecipientId(notificationId, currentUser.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Notification not found")
                );

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepo.save(notification);
        }
    }

    // 🔹 Mark all notifications as read
    public void markAllAsRead() {
        User currentUser = userService.getCurrentUser();
        notificationRepo.markAllAsRead(currentUser.getId());
    }
}
