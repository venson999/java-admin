package com.java.admin.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 8, max = 20, message = "Password length must be between 8 and 20")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    @Schema(description = "Password (must contain uppercase, lowercase, and digit)", example = "Password123")
    private String password;

    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "alice@example.com")
    private String email;
}
