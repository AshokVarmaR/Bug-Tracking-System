package com.bts.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bts.dtos.UserCreateRequestDTO;
import com.bts.dtos.UserResponseDTO;
import com.bts.enums.Role;
import com.bts.exceptions.ResourceNotFoundException;
import com.bts.mappers.UserMapper;
import com.bts.models.Otp;
import com.bts.models.User;
import com.bts.repositories.OtpRepository;
import com.bts.repositories.UserRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
@ConfigurationProperties("spring.mail")
public class UserService implements CommandLineRunner {

	@Getter @Setter
	private String username;

    private final JavaMailSender mailSender;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final OtpRepository otpRepo;

	@Override
	public void run(String... args) throws Exception {

		String username = "admin@gmail.com";

		if (!userRepository.existsByEmailOrEmployeeId(username, username)) {
			User admin = new User();
			admin.setName("Admin");
			admin.setEmail("admin@gmail.com");
			admin.setEmployeeId("EID1");
			admin.setRole(Role.ADMIN);
			admin.setPassword(passwordEncoder.encode("1234"));
			admin.setActive(true);
			userRepository.save(admin);
		}

	}

	public UserResponseDTO createUser(UserCreateRequestDTO dto) {

		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		User user = userMapper.toEntity(dto);

		// auto-generate employeeId
		user.setEmployeeId(generateEmployeeId());

		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setActive(true);

		User savedUser = userRepository.save(user);
		return userMapper.toResponse(savedUser);
	}

	public List<UserResponseDTO> getAllUsers() {
		return userMapper.toResponseList(userRepository.findAll());
	}

	public UserResponseDTO getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id " + id));

		return userMapper.toResponse(user);
	}

	public List<UserResponseDTO> getUsersByRole(Role role) {
		return userMapper.toResponseList(userRepository.findByRole(role));
	}

	public UserResponseDTO updateUser(Long id, UserCreateRequestDTO dto) {

		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		user.setRole(dto.getRole());

		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		return userMapper.toResponse(userRepository.save(user));
	}

	public void toggleUserStatus(Long id, boolean active) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

		user.setActive(active);
		userRepository.save(user);
	}

	public void deleteUser(Long id) {
		toggleUserStatus(id, false);
	}

	private String generateEmployeeId() {
		long count = userRepository.count() + 1;
		return "EID" + count;
	}
	
    public User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
	public boolean generateOtp(String email) {
	    Optional<User> optionalUser = userRepository.findByEmail(email);
	    
	    if (optionalUser.isPresent()) {
	        User user = optionalUser.get();
	        
	            // Generate 6-digit OTP
	            int otp = 100000 + new Random().nextInt(900000);
	            String otpString = Integer.toString(otp);

	            // Delete existing OTP for this user
	            otpRepo.findByUser_Id(user.getId()).ifPresent(otpRepo::delete);

	            // Create and save new OTP
	            Otp otpass = new Otp();
	            otpass.setPassword(passwordEncoder.encode(otpString)); 
	            otpass.setCreatedAt(LocalDateTime.now());
	            otpass.setUser(user); 
	            otpRepo.save(otpass); 

	            // Send OTP via email
	            SimpleMailMessage message = new SimpleMailMessage();
	            message.setTo(email);
	            message.setSubject("Password Reset");
	            message.setText("OTP to reset your Account password is " + otp +". Do not share this with anyone.");
	    		message.setFrom(username);
	    		mailSender.send(message);
	            System.out.println("Otp : "+otp);
	            return true;
	        
	    }

	    return false;
	}

	public boolean verifyOtp(String email, String otp) {
	    Optional<Otp> optionalOtp = otpRepo.findByUser_Email(email);
	    System.out.println("Iam in verify method");
	    if (optionalOtp.isPresent()) {
	        Otp ot = optionalOtp.get();
	        if (passwordEncoder.matches(otp, ot.getPassword())) {
	            return true;
	        }
	    }

	    return false;
	}

	public boolean resetPassword(String email, String password) {
		
		Optional<Otp> optionalOtp = otpRepo.findByUser_Email(email);
		if(optionalOtp.isPresent()){
			Otp otp = optionalOtp.get();
			otp.getUser().setPassword(passwordEncoder.encode(password));
			otpRepo.save(otp);
			otpRepo.delete(otp);
			return true;
		}		
		return false;
	}


}
