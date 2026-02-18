package com.bts.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bts.models.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

}
