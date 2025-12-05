package sk.cyrilgavala.wardrobeapi.item.application.dto;

import java.time.Instant;

import lombok.Builder;

/**
 * Application layer DTO for Item entity. This DTO is used to transfer item data from the
 * application layer to the presentation layer.
 */
@Builder
public record ItemDto(
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
    Boolean canBeBleached,
    String imageUrl, Integer boxNumber,
    Instant createdAt,
    Instant updatedAt
) {

}

