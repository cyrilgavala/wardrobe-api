package sk.cyrilgavala.wardrobeapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {

	@NotBlank
	private String username;
	@NotBlank
	private String password;
}
