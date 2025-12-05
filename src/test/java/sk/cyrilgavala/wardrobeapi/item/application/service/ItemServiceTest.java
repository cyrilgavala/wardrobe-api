package sk.cyrilgavala.wardrobeapi.item.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private ItemService itemService;

  private CreateItemCommand createCommand;
  private UpdateItemCommand updateCommand;
  private Item testItem;
  private final String userId = "user123";
  private final String itemId = "item123";

  @BeforeEach
  void setUp() {
    createCommand = new CreateItemCommand(
        userId,
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

    updateCommand = new UpdateItemCommand(
        itemId,
        userId,
        "Updated Jeans",
        "Updated description",
        ItemCategory.BOTTOMS,
        "Dark Blue",
        "Levi's",
        "32",
        40,
        true,
        false,
        false,
        false,
        "https://example.com/updated.jpg"
    );

    testItem = Item.builder()
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
        .build();
  }

  // ===== Create Tests =====

  @Test
  void createItem_shouldCreateItemSuccessfully() {
    // Given
    when(itemRepository.save(any(Item.class))).thenReturn(testItem);

    // When
    Item result = itemService.createItem(createCommand);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.name()).isEqualTo("Blue Jeans");
    assertThat(result.category()).isEqualTo(ItemCategory.BOTTOMS);

    ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
    verify(itemRepository).save(itemCaptor.capture());

    Item capturedItem = itemCaptor.getValue();
    assertThat(capturedItem.name()).isEqualTo("Blue Jeans");
    assertThat(capturedItem.userId()).isEqualTo(userId);
  }

  @Test
  void createItem_shouldSetCreatedAtAndUpdatedAt() {
    // Given
    when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
      Item item = invocation.getArgument(0);
      return Item.builder()
          .id("newId")
          .userId(item.userId())
          .name(item.name())
          .description(item.description())
          .category(item.category())
          .color(item.color())
          .brand(item.brand())
          .size(item.size())
          .washingTemperature(item.washingTemperature())
          .canBeIroned(item.canBeIroned())
          .canBeTumbleDried(item.canBeTumbleDried())
          .canBeDryCleaned(item.canBeDryCleaned())
          .canBeBleached(item.canBeBleached())
          .imageUrl(item.imageUrl())
          .createdAt(item.createdAt())
          .updatedAt(item.updatedAt())
          .build();
    });

    // When
    Item result = itemService.createItem(createCommand);

    // Then
    assertThat(result.createdAt()).isNotNull();
    assertThat(result.updatedAt()).isNotNull();
  }

  // ===== Update Tests =====

  @Test
  void updateItem_shouldUpdateItemSuccessfully() {
    // Given
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
    when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Item result = itemService.updateItem(updateCommand);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(itemId);
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.name()).isEqualTo("Updated Jeans");
    assertThat(result.description()).isEqualTo("Updated description");

    verify(itemRepository).findById(itemId);
    verify(itemRepository).save(any(Item.class));
  }

  @Test
  void updateItem_shouldThrowException_whenItemNotFound() {
    // Given
    when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> itemService.updateItem(updateCommand))
        .isInstanceOf(ItemNotFoundException.class)
        .hasMessageContaining(itemId);

    verify(itemRepository).findById(itemId);
    verify(itemRepository, never()).save(any(Item.class));
  }

  @Test
  void updateItem_shouldThrowException_whenUserDoesNotOwnItem() {
    // Given
    String differentUserId = "differentUser";
    UpdateItemCommand commandFromDifferentUser = new UpdateItemCommand(
        itemId,
        differentUserId,
        "Updated Jeans",
        "Updated description",
        ItemCategory.BOTTOMS,
        "Dark Blue",
        "Levi's",
        "32",
        40,
        true,
        false,
        false,
        false,
        "https://example.com/updated.jpg"
    );

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

    // When & Then
    assertThatThrownBy(() -> itemService.updateItem(commandFromDifferentUser))
        .isInstanceOf(ItemAccessDeniedException.class)
        .hasMessageContaining(itemId);

    verify(itemRepository).findById(itemId);
    verify(itemRepository, never()).save(any(Item.class));
  }

  // ===== Delete Tests =====

  @Test
  void deleteItem_shouldDeleteItemSuccessfully() {
    // Given
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

    // When
    itemService.deleteItem(itemId, userId);

    // Then
    verify(itemRepository).findById(itemId);
    verify(itemRepository).deleteById(itemId);
  }

  @Test
  void deleteItem_shouldThrowException_whenItemNotFound() {
    // Given
    when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> itemService.deleteItem(itemId, userId))
        .isInstanceOf(ItemNotFoundException.class)
        .hasMessageContaining(itemId);

    verify(itemRepository).findById(itemId);
    verify(itemRepository, never()).deleteById(itemId);
  }

  @Test
  void deleteItem_shouldThrowException_whenUserDoesNotOwnItem() {
    // Given
    String differentUserId = "differentUser";
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

    // When & Then
    assertThatThrownBy(() -> itemService.deleteItem(itemId, differentUserId))
        .isInstanceOf(ItemAccessDeniedException.class)
        .hasMessageContaining(itemId);

    verify(itemRepository).findById(itemId);
    verify(itemRepository, never()).deleteById(itemId);
  }

  // ===== Get Tests =====

  @Test
  void getItem_shouldReturnItemSuccessfully() {
    // Given
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

    // When
    Item result = itemService.getItem(itemId, userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(itemId);
    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.name()).isEqualTo("Blue Jeans");

    verify(itemRepository).findById(itemId);
  }

  @Test
  void getItem_shouldThrowException_whenItemNotFound() {
    // Given
    when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> itemService.getItem(itemId, userId))
        .isInstanceOf(ItemNotFoundException.class)
        .hasMessageContaining(itemId);

    verify(itemRepository).findById(itemId);
  }

  @Test
  void getItem_shouldThrowException_whenUserDoesNotOwnItem() {
    // Given
    String differentUserId = "differentUser";
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

    // When & Then
    assertThatThrownBy(() -> itemService.getItem(itemId, differentUserId))
        .isInstanceOf(ItemAccessDeniedException.class)
        .hasMessageContaining(itemId);

    verify(itemRepository).findById(itemId);
  }

  @Test
  void getAllItemsForUser_shouldReturnAllUserItems() {
    // Given
    Item item1 = Item.builder().id("item1").userId(userId).name("Item 1")
        .category(ItemCategory.TOPS).build();
    Item item2 = Item.builder().id("item2").userId(userId).name("Item 2")
        .category(ItemCategory.BOTTOMS).build();
    List<Item> userItems = List.of(item1, item2);

    when(itemRepository.findAllByUserId(userId)).thenReturn(userItems);

    // When
    List<Item> result = itemService.getAllItemsForUser(userId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(item1, item2);

    verify(itemRepository).findAllByUserId(userId);
  }

  @Test
  void getAllItemsForUser_shouldReturnEmptyList_whenNoItems() {
    // Given
    when(itemRepository.findAllByUserId(userId)).thenReturn(List.of());

    // When
    List<Item> result = itemService.getAllItemsForUser(userId);

    // Then
    assertThat(result).isEmpty();

    verify(itemRepository).findAllByUserId(userId);
  }

  @Test
  void getItemsByCategory_shouldReturnFilteredItems() {
    // Given
    ItemCategory category = ItemCategory.TOPS;
    Item item1 = Item.builder().id("item1").userId(userId).name("T-Shirt").category(category)
        .build();
    Item item2 = Item.builder().id("item2").userId(userId).name("Blouse").category(category)
        .build();
    List<Item> categoryItems = List.of(item1, item2);

    when(itemRepository.findByUserIdAndCategory(userId, category)).thenReturn(categoryItems);

    // When
    List<Item> result = itemService.getItemsByCategory(userId, category);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(item1, item2);
    assertThat(result).allMatch(item -> item.category() == category);

    verify(itemRepository).findByUserIdAndCategory(userId, category);
  }

  @Test
  void getItemsByCategory_shouldReturnEmptyList_whenNoCategoryItems() {
    // Given
    ItemCategory category = ItemCategory.FORMAL;
    when(itemRepository.findByUserIdAndCategory(userId, category)).thenReturn(List.of());

    // When
    List<Item> result = itemService.getItemsByCategory(userId, category);

    // Then
    assertThat(result).isEmpty();

    verify(itemRepository).findByUserIdAndCategory(userId, category);
  }
}

