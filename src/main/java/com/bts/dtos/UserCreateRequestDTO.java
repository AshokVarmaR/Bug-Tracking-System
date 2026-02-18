package com.bts.dtos;

import com.bts.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserCreateRequestDTO {
    private String name;
    private String email;
    private String password;
    private Role role;
}
