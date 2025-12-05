package sk.cyrilgavala.wardrobeapi.item.domain.model;

import java.time.Instant;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "items")
@Builder
public record Item(
    @Id
    String id,

    @Field("user_id")
    String userId,

    @Field("name")
    String name,

    @Field("description")
    String description,

    @Field("category")
    Category category,

    @Field("room")
    Room room,

    @Field("color")
    String color,

    @Field("brand")
    String brand,

    @Field("size")
    String size,

    @Field("washing_temperature")
    Integer washingTemperature,

    @Field("can_be_ironed")
    Boolean canBeIroned,

    @Field("can_be_tumble_dried")
    Boolean canBeTumbleDried,

    @Field("can_be_dry_cleaned")
    Boolean canBeDryCleaned,

    @Field("can_be_bleached")
    Boolean canBeBleached,

    @Field("image_url")
    String imageUrl,

    @Field("created_at")
    Instant createdAt,

    @Field("updated_at")
    Instant updatedAt
) {

  public static Item create(
      String userId,
      String name,
      String description,
      Category category,
      Room room,
      String color,
      String brand,
      String size,
      Integer washingTemperature,
      Boolean canBeIroned,
      Boolean canBeTumbleDried,
      Boolean canBeDryCleaned,
      Boolean canBeBleached,
      String imageUrl) {
    return Item.builder()
        .userId(userId)
        .name(name)
        .description(description)
        .category(category)
        .room(room)
        .color(color)
        .brand(brand)
        .size(size)
        .washingTemperature(washingTemperature)
        .canBeIroned(canBeIroned)
        .canBeTumbleDried(canBeTumbleDried)
        .canBeDryCleaned(canBeDryCleaned)
        .canBeBleached(canBeBleached)
        .imageUrl(imageUrl)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  public Item update(
      String name,
      String description,
      Category category,
      Room room,
      String color,
      String brand,
      String size,
      Integer washingTemperature,
      Boolean canBeIroned,
      Boolean canBeTumbleDried,
      Boolean canBeDryCleaned,
      Boolean canBeBleached,
      String imageUrl) {
    return Item.builder()
        .id(this.id)
        .userId(this.userId)
        .name(name)
        .description(description)
        .category(category)
        .room(room)
        .color(color)
        .brand(brand)
        .size(size)
        .washingTemperature(washingTemperature)
        .canBeIroned(canBeIroned)
        .canBeTumbleDried(canBeTumbleDried)
        .canBeDryCleaned(canBeDryCleaned)
        .canBeBleached(canBeBleached)
        .imageUrl(imageUrl)
        .createdAt(this.createdAt)
        .updatedAt(Instant.now())
        .build();
  }
}

