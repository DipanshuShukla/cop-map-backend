package com.dipanshushukla.cop_map_auth_service.dto;

import com.dipanshushukla.cop_map_auth_service.entity.User;
import com.dipanshushukla.cop_map_auth_service.model.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotNull(message = "badgeNumber must not be null")
    @NotBlank(message = "badgeNumber must not be blank")
    @Size(min = 3, max = 20, message = "badgeNumber must be between 3 and 20 characters")
    private String badgeNumber;

    @NotNull(message = "fullName must not be null")
    @NotBlank(message = "fullName must not be blank")
    @Size(min = 2, message = "fullName must be at least 2 characters long")
    private String fullName;

    @NotNull(message = "password must not be null")
    @NotBlank(message = "password must not be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,64}$", message = "password must be 8–64 characters, contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @NotNull(message = "email must not be null")
    @NotBlank(message = "email must not be blank")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email address")
    private String email;

    @NotNull(message = "phoneNumber must not be null")
    @NotBlank(message = "phoneNumber must not be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "phoneNumber must be a valid 10-digit number")
    private String phoneNumber;

    @NotNull(message = "thanaId must not be null")
    @NotBlank(message = "thanaId must not be blank")
    private String thanaId;

    private Role role;

    /** Convert Entity → DTO */
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .badgeNumber(user.getBadgeNumber())
                .fullName(user.getFullName())
                .password(null) // do NOT expose password
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .thanaId(user.getThanaId())
                .role(user.getRole())
                .build();
    }

    /** Convert DTO → Entity */
    public User toEntity(String encodedPassword) {
        return User.builder()
                .badgeNumber(badgeNumber)
                .fullName(fullName)
                .password(encodedPassword)
                .email(email)
                .phoneNumber(phoneNumber)
                .thanaId(thanaId)
                // Defaulting to the lowest privilege role in hierarchy
                .role(role != null ? role : Role.CONSTABLE)
                .build();
    }
}