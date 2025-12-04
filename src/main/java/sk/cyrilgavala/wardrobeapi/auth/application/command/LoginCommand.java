package sk.cyrilgavala.wardrobeapi.auth.application.command;

import lombok.Builder;

@Builder
public record LoginCommand(
    String username,
    String password
) {

}

