package com.java.admin.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * Request DTO for updating user information
 */
@Data
@Schema(description = "Request DTO for updating user information")
public class UpdateUserRequestDTO {

    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "newemail@example.com")
    private String email;
}
