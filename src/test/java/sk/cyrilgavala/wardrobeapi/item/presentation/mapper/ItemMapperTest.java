package sk.cyrilgavala.wardrobeapi.item.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.CreateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.ItemResponse;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.UpdateItemRequest;

class ItemMapperTest {

  private ItemMapper itemMapper;
  private final String userId = "user123";
  private final String itemId = "item123";

  @BeforeEach
  void setUp() {
    itemMapper = new ItemMapper();
  }

  // ===== CreateItemRequest to CreateItemCommand Tests =====

  @Test
  void toCreateCommand_shouldMapAllFields() {
    // Given
    CreateItemRequest request = new CreateItemRequest(
        "Blue Jeans",
        "Comfortable denim jeans",
        ItemCategory.BOTTOMS,
        "Blue",
        "Levi's",
        "32",
        40,
        true,
        false,
        false,
        false,
        "https://example.com/jeans.jpg"
    );

    // When
    CreateItemCommand command = itemMapper.toCreateCommand(request, userId);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.userId()).isEqualTo(userId);
    assertThat(command.name()).isEqualTo("Blue Jeans");
    assertThat(command.description()).isEqualTo("Comfortable denim jeans");
    assertThat(command.category()).isEqualTo(ItemCategory.BOTTOMS);
    assertThat(command.color()).isEqualTo("Blue");
    assertThat(command.brand()).isEqualTo("Levi's");
    assertThat(command.size()).isEqualTo("32");
    assertThat(command.washingTemperature()).isEqualTo(40);
    assertThat(command.canBeIroned()).isTrue();
    assertThat(command.canBeTumbleDried()).isFalse();
    assertThat(command.canBeDryCleaned()).isFalse();
    assertThat(command.canBeBleached()).isFalse();
    assertThat(command.imageUrl()).isEqualTo("https://example.com/jeans.jpg");
  }

  @Test
  void toCreateCommand_shouldHandleNullOptionalFields() {
    // Given
    CreateItemRequest request = new CreateItemRequest(
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

    // When
    CreateItemCommand command = itemMapper.toCreateCommand(request, userId);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.userId()).isEqualTo(userId);
    assertThat(command.name()).isEqualTo("Minimal Item");
    assertThat(command.description()).isNull();
    assertThat(command.color()).isNull();
    assertThat(command.brand()).isNull();
    assertThat(command.size()).isNull();
    assertThat(command.washingTemperature()).isNull();
    assertThat(command.canBeIroned()).isNull();
    assertThat(command.imageUrl()).isNull();
  }

  // ===== UpdateItemRequest to UpdateItemCommand Tests =====

  @Test
  void toUpdateCommand_shouldMapAllFields() {
    // Given
    UpdateItemRequest request = new UpdateItemRequest(
        "Updated Jeans",
        "Updated description",
        ItemCategory.BOTTOMS,
        "Dark Blue",
        "Levi's",
        "34",
        40,
        true,
        false,
        false,
        false,
        "https://example.com/updated.jpg"
    );

    // When
    UpdateItemCommand command = itemMapper.toUpdateCommand(request, itemId, userId);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.id()).isEqualTo(itemId);
    assertThat(command.userId()).isEqualTo(userId);
    assertThat(command.name()).isEqualTo("Updated Jeans");
    assertThat(command.description()).isEqualTo("Updated description");
    assertThat(command.category()).isEqualTo(ItemCategory.BOTTOMS);
    assertThat(command.color()).isEqualTo("Dark Blue");
    assertThat(command.brand()).isEqualTo("Levi's");
    assertThat(command.size()).isEqualTo("34");
    assertThat(command.washingTemperature()).isEqualTo(40);
    assertThat(command.canBeIroned()).isTrue();
    assertThat(command.canBeTumbleDried()).isFalse();
    assertThat(command.canBeDryCleaned()).isFalse();
    assertThat(command.canBeBleached()).isFalse();
    assertThat(command.imageUrl()).isEqualTo("https://example.com/updated.jpg");
  }

  @Test
  void toUpdateCommand_shouldHandleNullOptionalFields() {
    // Given
    UpdateItemRequest request = new UpdateItemRequest(
        "Updated Item",
        null,
        ItemCategory.TOPS,
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

    // When
    UpdateItemCommand command = itemMapper.toUpdateCommand(request, itemId, userId);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.id()).isEqualTo(itemId);
    assertThat(command.userId()).isEqualTo(userId);
    assertThat(command.name()).isEqualTo("Updated Item");
    assertThat(command.description()).isNull();
  }

  // ===== Item to ItemResponse Tests =====

  @Test
  void toResponse_shouldMapAllFields() {
    // Given
    Instant now = Instant.now();
    Item item = Item.builder()
        .id(itemId)
        .userId(userId)
        .name("Blue Jeans")
        .description("Comfortable denim jeans")
        .category(ItemCategory.BOTTOMS)
        .color("Blue")
        .brand("Levi's")
        .size("32")
        .washingTemperature(40)
        .canBeIroned(true)
        .canBeTumbleDried(false)
        .canBeDryCleaned(false)
        .canBeBleached(false)
        .imageUrl("https://example.com/jeans.jpg")
        .createdAt(now)
        .updatedAt(now)
        .build();

    // When
    ItemResponse response = itemMapper.toResponse(item);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(itemId);
    assertThat(response.userId()).isEqualTo(userId);
    assertThat(response.name()).isEqualTo("Blue Jeans");
    assertThat(response.description()).isEqualTo("Comfortable denim jeans");
    assertThat(response.category()).isEqualTo(ItemCategory.BOTTOMS);
    assertThat(response.color()).isEqualTo("Blue");
    assertThat(response.brand()).isEqualTo("Levi's");
    assertThat(response.size()).isEqualTo("32");
    assertThat(response.washingTemperature()).isEqualTo(40);
    assertThat(response.canBeIroned()).isTrue();
    assertThat(response.canBeTumbleDried()).isFalse();
    assertThat(response.canBeDryCleaned()).isFalse();
    assertThat(response.canBeBleached()).isFalse();
    assertThat(response.imageUrl()).isEqualTo("https://example.com/jeans.jpg");
    assertThat(response.createdAt()).isEqualTo(now);
    assertThat(response.updatedAt()).isEqualTo(now);
  }

  @Test
  void toResponse_shouldHandleNullOptionalFields() {
    // Given
    Instant now = Instant.now();
    Item item = Item.builder()
        .id(itemId)
        .userId(userId)
        .name("Minimal Item")
        .description(null)
        .category(ItemCategory.OTHER)
        .color(null)
        .brand(null)
        .size(null)
        .washingTemperature(null)
        .canBeIroned(null)
        .canBeTumbleDried(null)
        .canBeDryCleaned(null)
        .canBeBleached(null)
        .imageUrl(null)
        .createdAt(now)
        .updatedAt(now)
        .build();

    // When
    ItemResponse response = itemMapper.toResponse(item);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(itemId);
    assertThat(response.name()).isEqualTo("Minimal Item");
    assertThat(response.description()).isNull();
    assertThat(response.color()).isNull();
    assertThat(response.brand()).isNull();
    assertThat(response.size()).isNull();
    assertThat(response.washingTemperature()).isNull();
    assertThat(response.canBeIroned()).isNull();
  }
}

