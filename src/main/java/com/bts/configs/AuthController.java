package com.bts.configs;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bts.dtos.LoginRequestDTO;
import com.bts.dtos.LoginResponseDTO;
import com.bts.dtos.UserResponseDTO;
import com.bts.exceptions.ResourceNotFoundException;
import com.bts.mappers.UserMapper;
import com.bts.models.User;
import com.bts.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository UserRepository;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {

    	System.out.println(request.toString());
    	
        // username = email OR Phone
        Authentication r = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        System.out.println("Hi "+r.toString());

        // Fetch User using same identifier
        User user = UserRepository
                .findByEmailOrEmployeeId(
                        request.getUsername(),
                        request.getUsername()
                )
                .orElseThrow(() -> new BadCredentialsException("Invalid Credentials"));

        String token = jwtUtil.generateToken(user);
        
        System.out.println(user.getEmail());

        UserResponseDTO UserResponse = userMapper.toResponse(user);
        
        System.out.println(UserResponse.toString());
     
        return new LoginResponseDTO(token, UserResponse);
    }
}
