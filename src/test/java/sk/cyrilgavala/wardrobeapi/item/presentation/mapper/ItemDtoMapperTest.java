package sk.cyrilgavala.wardrobeapi.item.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.CreateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.ItemResponse;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.UpdateItemRequest;

class ItemDtoMapperTest {

  private ItemDtoMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ItemDtoMapper();
  }

  @Test
  void mapsCreateRequestToCommandWithAllFields() {
    CreateItemRequest request = new CreateItemRequest(
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

    CreateItemCommand result = mapper.toCreateCommand(request, "user123");

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(new CreateItemCommand(
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
        ));
  }

  @Test
  void mapsCreateRequestToCommandWithNullOptionalFields() {
    CreateItemRequest request = new CreateItemRequest(
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

    CreateItemCommand result = mapper.toCreateCommand(request, "user456");

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(new CreateItemCommand(
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
        ));
  }

  @Test
  void includesUserIdInCreateCommand() {
    CreateItemRequest request = new CreateItemRequest(
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

    CreateItemCommand result = mapper.toCreateCommand(request, "user789");

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(new CreateItemCommand(
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
        ));
  }

  @Test
  void mapsUpdateRequestToCommandWithAllFields() {
    UpdateItemRequest request = new UpdateItemRequest(
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

    UpdateItemCommand result = mapper.toUpdateCommand(request, "item123", "user123");

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(new UpdateItemCommand(
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
        ));
  }

  @Test
  void mapsUpdateRequestToCommandWithNullOptionalFields() {
    UpdateItemRequest request = new UpdateItemRequest(
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

    UpdateItemCommand result = mapper.toUpdateCommand(request, "item999", "user999");

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(new UpdateItemCommand(
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
        ));
  }

  @Test
  void includesItemIdAndUserIdInUpdateCommand() {
    UpdateItemRequest request = new UpdateItemRequest(
        "Item",
        "Description",
        "Color",
        "Brand",
        "M",
        40,
        true,
        false,
        false,
        null,
        3
    );

    UpdateItemCommand result = mapper.toUpdateCommand(request, "item456", "user456");

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(new UpdateItemCommand(
            "item456",
            "user456",
            "Item",
            "Description",
            "Color",
            "Brand",
            "M",
            40,
            true,
            false,
            false,
            null,
            3
        ));
  }

  @Test
  void mapsItemToResponseWithAllFields() {
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Blue Jeans")
        .description("Comfortable denim")
        .color("Blue")
        .brand("Levi's")
        .size("M")
        .washingTemperature(40)
        .canBeIroned(true)
        .canBeDried(false)
        .canBeBleached(false)
        .imageUrl("https://example.com/image.jpg")
        .boxNumber(5)
        .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
        .updatedAt(Instant.parse("2024-01-02T15:30:00Z"))
        .build();

    ItemResponse result = mapper.toResponse(item);

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(ItemResponse.of(
            "item123",
            "user123",
            "Blue Jeans",
            "Comfortable denim",
            "Blue",
            "Levi's",
            "M",
            40,
            true,
            false,
            false,
            "https://example.com/image.jpg",
            5,
            Instant.parse("2024-01-01T10:00:00Z"),
            Instant.parse("2024-01-02T15:30:00Z")
        ));
  }

  @Test
  void mapsItemToResponseWithNullOptionalFields() {
    Item item = Item.builder()
        .id("item456")
        .userId("user456")
        .name("T-Shirt")
        .description(null)
        .color(null)
        .brand(null)
        .size(null)
        .washingTemperature(null)
        .canBeIroned(null)
        .canBeDried(null)
        .canBeBleached(null)
        .imageUrl(null)
        .boxNumber(null)
        .createdAt(Instant.parse("2024-02-01T08:00:00Z"))
        .updatedAt(Instant.parse("2024-02-01T08:00:00Z"))
        .build();

    ItemResponse result = mapper.toResponse(item);

    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(ItemResponse.of(
            "item456",
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
            null,
            Instant.parse("2024-02-01T08:00:00Z"),
            Instant.parse("2024-02-01T08:00:00Z")
        ));
  }

  @Test
  void returnsNullWhenItemIsNull() {
    ItemResponse result = mapper.toResponse(null);

    assertThat(result).isNull();
  }

  @Test
  void mapsMultipleItemsToResponseList() {
    List<Item> items = List.of(
        Item.builder()
            .id("item1")
            .userId("user123")
            .name("Jeans")
            .description("Blue jeans")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build(),
        Item.builder()
            .id("item2")
            .userId("user123")
            .name("Shirt")
            .description("White shirt")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build(),
        Item.builder()
            .id("item3")
            .userId("user123")
            .name("Dress")
            .description("Red dress")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build()
    );

    List<ItemResponse> result = mapper.toResponseList(items);

    assertThat(result).hasSize(3);
    assertThat(result).extracting("id").containsExactly("item1", "item2", "item3");
    assertThat(result).extracting("name").containsExactly("Jeans", "Shirt", "Dress");
  }

  @Test
  void returnsEmptyListWhenItemListIsEmpty() {
    List<Item> items = Collections.emptyList();

    List<ItemResponse> result = mapper.toResponseList(items);

    assertThat(result).isEmpty();
  }

  @Test
  void returnsEmptyListWhenItemListIsNull() {
    List<ItemResponse> result = mapper.toResponseList(null);

    assertThat(result).isEmpty();
  }

  @Test
  void mapsEachItemInListCorrectly() {
    List<Item> items = List.of(
        Item.builder()
            .id("item1")
            .userId("user1")
            .name("Item1")
            .description("Desc1")
            .color("Red")
            .brand("Brand1")
            .size("S")
            .washingTemperature(30)
            .canBeIroned(true)
            .canBeDried(false)
            .canBeBleached(false)
            .imageUrl("url1")
            .boxNumber(1)
            .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
            .updatedAt(Instant.parse("2024-01-01T10:00:00Z"))
            .build()
    );

    List<ItemResponse> result = mapper.toResponseList(items);

    assertThat(result.get(0))
        .usingRecursiveComparison()
        .isEqualTo(ItemResponse.of(
            "item1",
            "user1",
            "Item1",
            "Desc1",
            "Red",
            "Brand1",
            "S",
            30,
            true,
            false,
            false,
            "url1",
            1,
            Instant.parse("2024-01-01T10:00:00Z"),
            Instant.parse("2024-01-01T10:00:00Z")
        ));
  }
}

