package sk.cyrilgavala.wardrobeapi.auth.application.command;

import lombok.Builder;

@Builder
public record RegisterCommand(
    String username,
    String email,
    String password,
    String firstName,
    String lastName
) {

}

