package com.bts.dtos;

import java.time.LocalDate;

import com.bts.enums.Role;
import com.bts.models.Project;
import com.bts.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectAssignmentResponseDTO {

	private Long id;

	private UserResponseDTO user;

	private ProjectResponseDTO project;

	private Role role;
	
	private boolean active;

	private LocalDate endDate;
}