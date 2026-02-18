package com.bts.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otps")
public class Otp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String password;
	private LocalDateTime createdAt;
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
}
