package com.dipanshushukla.cop_map_auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginCredentialsDTO {
    @NotBlank(message = "Badge Number cannot be empty")
    private String badgeNumber;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
