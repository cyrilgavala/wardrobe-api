package sk.cyrilgavala.wardrobeapi.auth.presentation.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterUserCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.dto.UserDto;
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

  public UserResponse toResponse(UserDto userDto) {
    if (userDto == null) {
      return null;
    }

    return UserResponse.builder()
        .id(userDto.id())
        .username(userDto.username())
        .email(userDto.email())
        .firstName(userDto.firstName())
        .lastName(userDto.lastName())
        .role(userDto.role().name())
        .createdAt(userDto.createdAt())
        .lastLoginAt(userDto.lastLoginAt())
        .build();
  }
}

