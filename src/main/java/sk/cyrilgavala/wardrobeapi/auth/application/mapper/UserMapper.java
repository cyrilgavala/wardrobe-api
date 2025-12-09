package sk.cyrilgavala.wardrobeapi.auth.application.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.auth.application.dto.UserDto;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;

/**
 * Mapper for converting between User domain entity and UserDto. This mapper is used within the
 * application layer to convert domain entities to DTOs.
 */
@Component
public class UserMapper {

  public UserDto toDto(User user) {
    if (user == null) {
      return null;
    }

    return UserDto.builder()
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

