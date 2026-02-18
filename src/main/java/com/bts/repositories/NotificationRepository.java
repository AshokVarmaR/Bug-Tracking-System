package com.bts.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bts.models.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	  List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

	    List<Notification> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(Long recipientId);

	    Long countByRecipientIdAndReadFalse(Long recipientId);

	    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

	    @Modifying
	    @Query("""
	        UPDATE Notification n
	        SET n.read = true
	        WHERE n.recipient.id = :recipientId
	        AND n.read = false
	    """)
	    int markAllAsRead(Long recipientId);
}
