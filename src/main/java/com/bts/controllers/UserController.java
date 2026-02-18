package com.bts.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bts.dtos.UserCreateRequestDTO;
import com.bts.dtos.UserResponseDTO;
import com.bts.enums.Role;
import com.bts.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @GetMapping("/roles")
    public Role[] roles() {
    	return Role.values();
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(
            @PathVariable Role role) {

        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
    

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody UserCreateRequestDTO dto) {

        return ResponseEntity.ok(userService.createUser(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserCreateRequestDTO dto) {

        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        userService.toggleUserStatus(id, active);
        return ResponseEntity.ok("User status updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }
    
	@PostMapping("/otp/send")
	public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> jsonObject) {
	    
	    String email = jsonObject.get("email");
	    boolean isGenerated = userService.generateOtp(email);

	    Map<String, String> response = new HashMap<>();
	    
	    if (isGenerated) {
	        response.put("message", "OTP Generated Successfully");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("error", "Failed to generate OTP");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}
	
	@PostMapping("/otp/verify")
	public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> jsonObject) {
	    
	    String email = jsonObject.get("email");
	    String otp = jsonObject.get("otp");

	    boolean isVerified = userService.verifyOtp(email, otp);
	    Map<String, String> response = new HashMap<>();

	    if (isVerified) {
	        response.put("message", "OTP verified successfully");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("message", "Invalid OTP");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}

	
	@PostMapping("/password/reset")
	public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> jsonObject) {
	    
	    String email = jsonObject.get("email");
	    String password = jsonObject.get("password");

	    boolean isReset = userService.resetPassword(email, password);
	    Map<String, String> response = new HashMap<>();

	    if (isReset) {
	        response.put("message", "Password reset successfully");
	        return ResponseEntity.ok(response);
	    } else {
	        response.put("message", "Password didn't reset");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}
}
