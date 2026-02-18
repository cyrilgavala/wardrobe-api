package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "Request to register a new user account")
public record RegisterRequest(

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name of the user", example = "Doe")
    String lastName,

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @NotBlank(message = "First name is required")
    @Schema(description = "First name of the user", example = "John")
    String firstName,

    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters with uppercase, lowercase, and number")
    @Schema(description = "User password (min 8 characters, must contain uppercase, lowercase, and number)",
        example = "SecurePassword123!")
    String password,

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    String email,

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @NotBlank(message = "Username is required")
    @Schema(description = "Unique username for the account", example = "johndoe")
    String username
) {

}





