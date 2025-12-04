package sk.cyrilgavala.wardrobeapi.auth.presentation.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterUserCommand;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.UserResponse;

@Component
public class UserMapper {

  public RegisterUserCommand toCommand(RegisterRequest request) {
    return RegisterUserCommand.builder()
        .username(request.username())
        .email(request.email())
        .password(request.password())
        .firstName(request.firstName())
        .lastName(request.lastName())
        .build();
  }

  public LoginCommand toCommand(LoginRequest request) {
    return LoginCommand.builder()
        .username(request.username())
        .password(request.password())
        .build();
  }

  public UserResponse toResponse(User user) {
    return UserResponse.builder()
        .id(user.id())
        .username(user.username())
        .email(user.email())
        .firstName(user.firstName())
        .lastName(user.lastName())
        .role(user.role())
        .createdAt(user.createdAt())
        .lastLoginAt(user.lastLoginAt())
        .build();
  }
}

