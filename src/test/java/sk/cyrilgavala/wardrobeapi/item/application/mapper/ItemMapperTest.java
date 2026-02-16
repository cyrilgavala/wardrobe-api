package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;

class ItemMapperTest {

  private ItemMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ItemMapper();
  }

  @Test
  void mapsCreateCommandToItemWithAllFields() {
    CreateItemCommand command = new CreateItemCommand(
        "user123",
        "Blue Jeans",
        "Comfortable denim jeans",
        "Blue",
        "Levi's",
        "M",
        40,
        true,
        false,
        false,
        "https://example.com/image.jpg",
        5
    );

    Item result = mapper.fromCreateCommand(command);

    assertThat(result).isNotNull();
    assertThat(result.userId()).isEqualTo("user123");
    assertThat(result.name()).isEqualTo("Blue Jeans");
    assertThat(result.description()).isEqualTo("Comfortable denim jeans");
    assertThat(result.color()).isEqualTo("Blue");
    assertThat(result.brand()).isEqualTo("Levi's");
    assertThat(result.size()).isEqualTo("M");
    assertThat(result.washingTemperature()).isEqualTo(40);
    assertThat(result.canBeIroned()).isTrue();
    assertThat(result.canBeDried()).isFalse();
    assertThat(result.canBeBleached()).isFalse();
    assertThat(result.imageUrl()).isEqualTo("https://example.com/image.jpg");
    assertThat(result.boxNumber()).isEqualTo(5);
  }

  @Test
  void mapsCreateCommandWithNullOptionalFields() {
    CreateItemCommand command = new CreateItemCommand(
        "user456",
        "T-Shirt",
        null,
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

    Item result = mapper.fromCreateCommand(command);

    assertThat(result).isNotNull();
    assertThat(result.userId()).isEqualTo("user456");
    assertThat(result.name()).isEqualTo("T-Shirt");
    assertThat(result.description()).isNull();
    assertThat(result.color()).isNull();
    assertThat(result.brand()).isNull();
    assertThat(result.size()).isNull();
    assertThat(result.washingTemperature()).isNull();
    assertThat(result.canBeIroned()).isNull();
    assertThat(result.canBeDried()).isNull();
    assertThat(result.canBeBleached()).isNull();
    assertThat(result.imageUrl()).isNull();
    assertThat(result.boxNumber()).isNull();
  }

  @Test
  void setsTimestampsWhenCreatingItem() {
    CreateItemCommand command = new CreateItemCommand(
        "user789",
        "Dress",
        "Summer dress",
        "Red",
        "Zara",
        "S",
        30,
        true,
        false,
        false,
        null,
        null
    );
    Instant before = Instant.now();

    Item result = mapper.fromCreateCommand(command);

    Instant after = Instant.now();
    assertThat(result.createdAt()).isNotNull();
    assertThat(result.updatedAt()).isNotNull();
    assertThat(result.createdAt()).isBetween(before, after);
    assertThat(result.updatedAt()).isBetween(before, after);
  }

  @Test
  void mapsUpdateCommandToItemWithAllFields() {
    Item existingItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Old Name")
        .description("Old description")
        .createdAt(Instant.now().minusSeconds(3600))
        .build();
    UpdateItemCommand command = new UpdateItemCommand(
        "item123",
        "user123",
        "Updated Jeans",
        "New description",
        "Dark Blue",
        "Levi's",
        "L",
        60,
        false,
        true,
        false,
        "https://new-image.jpg",
        7
    );

    Item result = mapper.fromUpdateCommand(existingItem, command);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo("item123");
    assertThat(result.userId()).isEqualTo("user123");
    assertThat(result.name()).isEqualTo("Updated Jeans");
    assertThat(result.description()).isEqualTo("New description");
    assertThat(result.color()).isEqualTo("Dark Blue");
    assertThat(result.brand()).isEqualTo("Levi's");
    assertThat(result.size()).isEqualTo("L");
    assertThat(result.washingTemperature()).isEqualTo(60);
    assertThat(result.canBeIroned()).isFalse();
    assertThat(result.canBeDried()).isTrue();
    assertThat(result.canBeBleached()).isFalse();
    assertThat(result.imageUrl()).isEqualTo("https://new-image.jpg");
    assertThat(result.boxNumber()).isEqualTo(7);
  }

  @Test
  void preservesIdAndUserIdWhenUpdating() {
    Item existingItem = Item.builder()
        .id("item456")
        .userId("user456")
        .name("Original")
        .createdAt(Instant.now().minusSeconds(7200))
        .build();
    UpdateItemCommand command = new UpdateItemCommand(
        "item456",
        "user456",
        "Updated",
        "New desc",
        "Blue",
        "Brand",
        "M",
        40,
        true,
        false,
        false,
        null,
        null
    );

    Item result = mapper.fromUpdateCommand(existingItem, command);

    assertThat(result.id()).isEqualTo("item456");
    assertThat(result.userId()).isEqualTo("user456");
  }

  @Test
  void preservesCreatedAtButUpdatesUpdatedAt() {
    Instant originalCreatedAt = Instant.now().minusSeconds(10000);
    Item existingItem = Item.builder()
        .id("item789")
        .userId("user789")
        .name("Item")
        .createdAt(originalCreatedAt)
        .updatedAt(Instant.now().minusSeconds(5000))
        .build();
    UpdateItemCommand command = new UpdateItemCommand(
        "item789",
        "user789",
        "Updated Item",
        "Description",
        "Red",
        "Brand",
        "S",
        30,
        true,
        true,
        false,
        null,
        null
    );
    Instant beforeUpdate = Instant.now();

    Item result = mapper.fromUpdateCommand(existingItem, command);

    Instant afterUpdate = Instant.now();
    assertThat(result.createdAt()).isEqualTo(originalCreatedAt);
    assertThat(result.updatedAt()).isNotNull();
    assertThat(result.updatedAt()).isBetween(beforeUpdate, afterUpdate);
    assertThat(result.updatedAt()).isAfter(result.createdAt());
  }

  @Test
  void mapsUpdateCommandWithNullOptionalFields() {
    Item existingItem = Item.builder()
        .id("item999")
        .userId("user999")
        .name("Item")
        .createdAt(Instant.now())
        .build();
    UpdateItemCommand command = new UpdateItemCommand(
        "item999",
        "user999",
        "Updated Name",
        null,
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

    Item result = mapper.fromUpdateCommand(existingItem, command);

    assertThat(result)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(Item.builder()
            .id("item999")
            .userId("user999")
            .name("Updated Name")
            .description(null)
            .color(null)
            .brand(null)
            .build());
  }
}

