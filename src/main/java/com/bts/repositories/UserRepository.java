package com.bts.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bts.enums.Role;
import com.bts.models.Project;
import com.bts.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

//	Optional<User> findByEmailOrPhone(String username, String username2);

		Optional<User> findByEmailOrEmployeeId(String email, String empId);

		boolean existsByEmailOrEmployeeId(String username, String username2);

		boolean existsByEmail(String email);

		List<User> findByRole(Role role);

		Optional<User> findByEmail(String email);

}
