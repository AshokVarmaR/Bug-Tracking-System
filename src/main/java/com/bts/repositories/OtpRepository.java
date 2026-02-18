package com.bts.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bts.models.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long> {

	Optional<Otp> findByUser_Id(Long id);

	Optional<Otp> findByUser_Email(String email);
}
