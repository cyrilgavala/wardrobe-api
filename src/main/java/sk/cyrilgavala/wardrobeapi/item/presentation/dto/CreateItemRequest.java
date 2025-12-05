package sk.cyrilgavala.wardrobeapi.item.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;

@Schema(description = "Request to create a new wardrobe item")
public record CreateItemRequest(
    @Schema(description = "Name of the item", example = "Blue Denim Jeans")
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @Schema(description = "Description of the item", example = "Comfortable slim-fit jeans")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @Schema(description = "Category of the item", example = "BOTTOMS")
    @NotNull(message = "Category is required")
    ItemCategory category,

    @Schema(description = "Color of the item", example = "Blue")
    @Size(max = 50, message = "Color must not exceed 50 characters")
    String color,

    @Schema(description = "Brand of the item", example = "Levi's")
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    String brand,

    @Schema(description = "Size of the item", example = "M")
    @Size(max = 20, message = "Size must not exceed 20 characters")
    String size,

    @Schema(description = "Maximum washing temperature in Celsius", example = "40")
    @Min(value = 0, message = "Washing temperature must be at least 0")
    @Max(value = 95, message = "Washing temperature must not exceed 95")
    Integer washingTemperature,

    @Schema(description = "Whether the item can be ironed", example = "true")
    Boolean canBeIroned,

    @Schema(description = "Whether the item can be tumble dried", example = "false")
    Boolean canBeTumbleDried,

    @Schema(description = "Whether the item can be dry cleaned", example = "false")
    Boolean canBeDryCleaned,

    @Schema(description = "Whether the item can be bleached", example = "false")
    Boolean canBeBleached,

    @Schema(description = "URL of the item image", example = "https://example.com/image.jpg")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    String imageUrl
) {

}


