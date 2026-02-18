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
    Boolean canBeDried,
    @Field("can_be_bleached")
    Boolean canBeBleached,
    @Field("image_id")
    String imageId,
    @Field("box_number")
    Integer boxNumber,
    @Field("created_at")
    Instant createdAt,
    @Field("updated_at")
    Instant updatedAt
) {

  public static Item create(
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
      String imageId,
      Integer boxNumber) {
    return Item.builder()
        .userId(userId)
        .name(name)
        .description(description)
        .color(color)
        .brand(brand)
        .size(size)
        .washingTemperature(washingTemperature)
        .canBeIroned(canBeIroned)
        .canBeDried(canBeDried)
        .canBeBleached(canBeBleached)
        .imageId(imageId)
        .boxNumber(boxNumber)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  public Item update(
      String name,
      String description,
      String color,
      String brand,
      String size,
      Integer washingTemperature,
      Boolean canBeIroned,
      Boolean canBeDried,
      Boolean canBeBleached,
      String imageId,
      Integer boxNumber) {
    return Item.builder()
        .id(this.id)
        .userId(this.userId)
        .name(name)
        .description(description)
        .color(color)
        .brand(brand)
        .size(size)
        .washingTemperature(washingTemperature)
        .canBeIroned(canBeIroned)
        .canBeDried(canBeDried)
        .canBeBleached(canBeBleached)
        .imageId(imageId)
        .boxNumber(boxNumber)
        .createdAt(this.createdAt)
        .updatedAt(Instant.now())
        .build();
  }
}

