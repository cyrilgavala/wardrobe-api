package sk.cyrilgavala.wardrobeapi.item.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ItemTest {

  @Test
  void create_shouldCreateItemWithAllFields() {
    // Given
    String userId = "user123";
    String name = "Blue Jeans";
    String description = "Comfortable denim jeans";
    ItemCategory category = ItemCategory.BOTTOMS;
    String color = "Blue";
    String brand = "Levi's";
    String size = "32";
    Integer washingTemperature = 40;
    Boolean canBeIroned = true;
    Boolean canBeTumbleDried = false;
    Boolean canBeDryCleaned = false;
    Boolean canBeBleached = false;
    String imageUrl = "https://example.com/jeans.jpg";

    // When
    Item item = Item.create(
        userId,
        name,
        description,
        category,
        color,
        brand,
        size,
        washingTemperature,
        canBeIroned,
        canBeTumbleDried,
        canBeDryCleaned,
        canBeBleached,
        imageUrl
    );

    // Then
    assertThat(item).isNotNull();
    assertThat(item.userId()).isEqualTo(userId);
    assertThat(item.name()).isEqualTo(name);
    assertThat(item.description()).isEqualTo(description);
    assertThat(item.category()).isEqualTo(category);
    assertThat(item.color()).isEqualTo(color);
    assertThat(item.brand()).isEqualTo(brand);
    assertThat(item.size()).isEqualTo(size);
    assertThat(item.washingTemperature()).isEqualTo(washingTemperature);
    assertThat(item.canBeIroned()).isTrue();
    assertThat(item.canBeTumbleDried()).isFalse();
    assertThat(item.canBeDryCleaned()).isFalse();
    assertThat(item.canBeBleached()).isFalse();
    assertThat(item.imageUrl()).isEqualTo(imageUrl);
    assertThat(item.createdAt()).isNotNull();
    assertThat(item.updatedAt()).isNotNull();
    assertThat(item.id()).isNull();
  }

  @Test
  void create_shouldSetCreatedAtAndUpdatedAt() {
    // Given
    Instant beforeCreation = Instant.now().minusSeconds(1);

    // When
    Item item = Item.create(
        "user123",
        "Test Item",
        "Description",
        ItemCategory.TOPS,
        "Red",
        "Nike",
        "M",
        30,
        true,
        true,
        false,
        false,
        null
    );

    // Then
    Instant afterCreation = Instant.now().plusSeconds(1);
    assertThat(item.createdAt()).isAfter(beforeCreation);
    assertThat(item.createdAt()).isBefore(afterCreation);
    assertThat(item.updatedAt()).isAfter(beforeCreation);
    assertThat(item.updatedAt()).isBefore(afterCreation);
    assertThat(item.createdAt()).isEqualTo(item.updatedAt());
  }

  @Test
  void update_shouldUpdateAllFieldsExceptIdAndUserId() {
    // Given
    Item originalItem = Item.create(
        "user123",
        "Original Name",
        "Original Description",
        ItemCategory.TOPS,
        "Red",
        "Nike",
        "M",
        30,
        true,
        true,
        false,
        false,
        "https://example.com/original.jpg"
    );

    // Simulate the item being saved with an ID
    Item savedItem = Item.builder()
        .id("item123")
        .userId(originalItem.userId())
        .name(originalItem.name())
        .description(originalItem.description())
        .category(originalItem.category())
        .color(originalItem.color())
        .brand(originalItem.brand())
        .size(originalItem.size())
        .washingTemperature(originalItem.washingTemperature())
        .canBeIroned(originalItem.canBeIroned())
        .canBeTumbleDried(originalItem.canBeTumbleDried())
        .canBeDryCleaned(originalItem.canBeDryCleaned())
        .canBeBleached(originalItem.canBeBleached())
        .imageUrl(originalItem.imageUrl())
        .createdAt(originalItem.createdAt())
        .updatedAt(originalItem.updatedAt())
        .build();

    // When
    Item updatedItem = savedItem.update(
        "Updated Name",
        "Updated Description",
        ItemCategory.BOTTOMS,
        "Blue",
        "Adidas",
        "L",
        40,
        false,
        false,
        true,
        true,
        "https://example.com/updated.jpg"
    );

    // Then
    assertThat(updatedItem.id()).isEqualTo("item123");
    assertThat(updatedItem.userId()).isEqualTo("user123");
    assertThat(updatedItem.name()).isEqualTo("Updated Name");
    assertThat(updatedItem.description()).isEqualTo("Updated Description");
    assertThat(updatedItem.category()).isEqualTo(ItemCategory.BOTTOMS);
    assertThat(updatedItem.color()).isEqualTo("Blue");
    assertThat(updatedItem.brand()).isEqualTo("Adidas");
    assertThat(updatedItem.size()).isEqualTo("L");
    assertThat(updatedItem.washingTemperature()).isEqualTo(40);
    assertThat(updatedItem.canBeIroned()).isFalse();
    assertThat(updatedItem.canBeTumbleDried()).isFalse();
    assertThat(updatedItem.canBeDryCleaned()).isTrue();
    assertThat(updatedItem.canBeBleached()).isTrue();
    assertThat(updatedItem.imageUrl()).isEqualTo("https://example.com/updated.jpg");
    assertThat(updatedItem.createdAt()).isEqualTo(savedItem.createdAt());
    assertThat(updatedItem.updatedAt()).isAfter(savedItem.updatedAt());
  }

  @Test
  void update_shouldUpdateUpdatedAtTimestamp() throws InterruptedException {
    // Given
    Item originalItem = Item.create(
        "user123",
        "Original Name",
        "Original Description",
        ItemCategory.TOPS,
        "Red",
        "Nike",
        "M",
        30,
        true,
        true,
        false,
        false,
        null
    );

    Item savedItem = Item.builder()
        .id("item123")
        .userId(originalItem.userId())
        .name(originalItem.name())
        .description(originalItem.description())
        .category(originalItem.category())
        .color(originalItem.color())
        .brand(originalItem.brand())
        .size(originalItem.size())
        .washingTemperature(originalItem.washingTemperature())
        .canBeIroned(originalItem.canBeIroned())
        .canBeTumbleDried(originalItem.canBeTumbleDried())
        .canBeDryCleaned(originalItem.canBeDryCleaned())
        .canBeBleached(originalItem.canBeBleached())
        .imageUrl(originalItem.imageUrl())
        .createdAt(originalItem.createdAt())
        .updatedAt(originalItem.updatedAt())
        .build();

    Thread.sleep(10); // Ensure time difference

    // When
    Item updatedItem = savedItem.update(
        "Updated Name",
        "Updated Description",
        ItemCategory.BOTTOMS,
        "Blue",
        "Adidas",
        "L",
        40,
        false,
        false,
        true,
        true,
        null
    );

    // Then
    assertThat(updatedItem.updatedAt()).isAfter(savedItem.updatedAt());
    assertThat(updatedItem.createdAt()).isEqualTo(savedItem.createdAt());
  }

  @Test
  void create_shouldHandleNullOptionalFields() {
    // When
    Item item = Item.create(
        "user123",
        "Minimal Item",
        null,
        ItemCategory.OTHER,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    // Then
    assertThat(item).isNotNull();
    assertThat(item.name()).isEqualTo("Minimal Item");
    assertThat(item.description()).isNull();
    assertThat(item.color()).isNull();
    assertThat(item.brand()).isNull();
    assertThat(item.size()).isNull();
    assertThat(item.washingTemperature()).isNull();
    assertThat(item.canBeIroned()).isNull();
    assertThat(item.canBeTumbleDried()).isNull();
    assertThat(item.canBeDryCleaned()).isNull();
    assertThat(item.canBeBleached()).isNull();
    assertThat(item.imageUrl()).isNull();
  }
}

