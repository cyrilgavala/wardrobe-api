package sk.cyrilgavala.wardrobeapi.item.application.command;

public record UpdateItemCommand(
    String id,
    String userId,
    String name,
    String description,
    String category,
    String room,
    String color,
    String brand,
    String size,
    Integer washingTemperature,
    Boolean canBeIroned,
    Boolean canBeTumbleDried,
    Boolean canBeDryCleaned,
    Boolean canBeBleached, String imageUrl, Integer boxNumber
) {

}

