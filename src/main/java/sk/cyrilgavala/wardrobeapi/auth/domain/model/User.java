package sk.cyrilgavala.wardrobeapi.auth.domain.model;

import java.time.Instant;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
@Builder
public record User(
    @Id
    String id,

    @Indexed(unique = true)
    @Field("username")
    String username,

    @Indexed(unique = true)
    @Field("email")
    String email,

    @Field("password")
    String password,

    @Field("first_name")
    String firstName,

    @Field("last_name")
    String lastName,

    @Field("role")
    UserRole role,

    @Field("created_at")
    Instant createdAt,

    @Field("updated_at")
    Instant updatedAt,

    @Field("last_login_at")
    Instant lastLoginAt
) {

  // Factory method for creating new users
  public static User create(
      String username,
      String email,
      String password,
      String firstName,
      String lastName) {
    return User.builder()
        .username(username)
        .email(email)
        .password(password)
        .firstName(firstName)
        .lastName(lastName)
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  // Business methods returning new instances (records are immutable)
  public User recordLogin() {
    return User.builder()
        .id(this.id)
        .username(this.username)
        .email(this.email)
        .password(this.password)
        .firstName(this.firstName)
        .lastName(this.lastName)
        .role(this.role)
        .createdAt(this.createdAt)
        .updatedAt(this.updatedAt)
        .lastLoginAt(Instant.now())
        .build();
  }

  public User promoteToAdmin() {
    return User.builder()
        .id(this.id)
        .username(this.username)
        .email(this.email)
        .password(this.password)
        .firstName(this.firstName)
        .lastName(this.lastName)
        .role(UserRole.ADMIN)
        .createdAt(this.createdAt)
        .updatedAt(Instant.now())
        .lastLoginAt(this.lastLoginAt)
        .build();
  }
}

