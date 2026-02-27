package com.java.admin.modules.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for batch deleting users
 */
@Data
@Schema(description = "Request DTO for batch deleting users")
public class DeleteUsersRequestDTO {

    @NotEmpty(message = "IDs cannot be empty")
    @Size(min = 1, max = 100, message = "Batch size must be between 1 and 100")
    @Schema(description = "List of user IDs to delete (max 100)", example = "[\"id1\", \"id2\", \"id3\"]")
    private List<@NotBlank(message = "ID cannot be blank") String> ids;
}
