package sk.cyrilgavala.wardrobeapi.web.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = -3333137047634067175L;

	private String id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	@JsonIgnore
	private String password;
	private String role;
}