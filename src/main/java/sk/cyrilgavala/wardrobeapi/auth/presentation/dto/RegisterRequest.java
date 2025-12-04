package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @NotBlank(message = "Last name is required")
    String lastName,

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @NotBlank(message = "First name is required")
    String firstName,

    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters with uppercase, lowercase, and number")
    String password,

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    String email,

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @NotBlank(message = "Username is required")
    String username
) {

}





