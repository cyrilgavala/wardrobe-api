package sk.cyrilgavala.wardrobeapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	private String id;

	@Indexed(unique = true)
	@Field("username")
	private String username;

	@Field("first_name")
	private String firstName;

	@Field("last_name")
	private String lastName;

	@Indexed(unique = true)
	@Field("email")
	private String email;

	@Field("password")
	private String password;

	@Field("role")
	private String role;
}
