package sk.cyrilgavala.wardrobeapi.item.application.command;

public record CreateItemCommand(
    String userId,
    String name,
    String description,
    String color,
    String brand,
    String size,
    Integer washingTemperature,
    Boolean canBeIroned,
    Boolean canBeDried,
    Boolean canBeBleached,
    String imageUrl,
    Integer boxNumber
) {

}