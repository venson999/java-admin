package com.java.admin.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for creating a new user
 */
@Data
@Schema(description = "Request DTO for creating a new user")
public class CreateUserRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username length must be between 3 and 30")
    @Schema(description = "Username", example = "alice")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password length must be between 6 and 20")
    @Schema(description = "Password", example = "Password123")
    private String password;

    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "alice@example.com")
    private String email;
}
