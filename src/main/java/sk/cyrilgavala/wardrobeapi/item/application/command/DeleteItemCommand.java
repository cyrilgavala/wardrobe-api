package sk.cyrilgavala.wardrobeapi.item.application.command;

public record DeleteItemCommand(
    String id,
    String userId
) {

}

