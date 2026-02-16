package sk.cyrilgavala.wardrobeapi.item.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Response containing wardrobe item details")
public record ItemResponse(
    @Schema(description = "Unique identifier of the item", example = "507f1f77bcf86cd799439011")
    String id,

    @Schema(description = "User ID who owns the item", example = "507f1f77bcf86cd799439012")
    String userId,

    @Schema(description = "Name of the item", example = "Blue Denim Jeans")
    String name,

    @Schema(description = "Description of the item", example = "Comfortable slim-fit jeans")
    String description,

    @Schema(description = "Color of the item", example = "Blue")
    String color,

    @Schema(description = "Brand of the item", example = "Levi's")
    String brand,

    @Schema(description = "Size of the item", example = "M")
    String size,

    @Schema(description = "Maximum washing temperature in Celsius", example = "40")
    Integer washingTemperature,

    @Schema(description = "Whether the item can be ironed", example = "true")
    Boolean canBeIroned,

    @Schema(description = "Whether the item can be dried", example = "false")
    Boolean canBeDried,

    @Schema(description = "Whether the item can be bleached", example = "false")
    Boolean canBeBleached,

    @Schema(description = "URL of the item image", example = "https://example.com/image.jpg")
    String imageUrl,

    @Schema(description = "Box number where the item is stored", example = "1")
    Integer boxNumber,

    @Schema(description = "Date when the item was created", example = "2023-12-01T10:00:00Z")
    Instant createdAt,

    @Schema(description = "Date when the item was last updated", example = "2023-12-05T15:30:00Z")
    Instant updatedAt
) {

  public static ItemResponse of(
      String id,
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
      Integer boxNumber,
      Instant createdAt,
      Instant updatedAt) {
    return new ItemResponse(
        id,
        userId,
        name,
        description,
        color,
        brand,
        size,
        washingTemperature,
        canBeIroned,
        canBeDried,
        canBeBleached,
        imageUrl,
        boxNumber,
        createdAt,
        updatedAt
    );
  }
}

