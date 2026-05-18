package com.dipanshushukla.cop_map_auth_service.dto;

import com.dipanshushukla.cop_map_auth_service.model.Role;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(min = 2, message = "fullName must be at least 2 characters long")
    private String fullName;

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email address")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "phoneNumber must be a valid 10-digit number")
    private String phoneNumber;

    private String thanaId;

    private Role role;

}