package sk.cyrilgavala.wardrobeapi.item.application.command;

import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;

public record CreateItemCommand(
    String userId,
    String name,
    String description,
    ItemCategory category,
    String color,
    String brand,
    String size,
    Integer washingTemperature,
    Boolean canBeIroned,
    Boolean canBeTumbleDried,
    Boolean canBeDryCleaned,
    Boolean canBeBleached,
    String imageUrl
) {

}