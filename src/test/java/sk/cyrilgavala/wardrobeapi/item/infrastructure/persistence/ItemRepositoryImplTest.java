package sk.cyrilgavala.wardrobeapi.item.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;

@ExtendWith(MockitoExtension.class)
class ItemRepositoryImplTest {

  @Mock
  private MongoItemRepository mongoItemRepository;

  @InjectMocks
  private ItemRepositoryImpl itemRepository;

  private Item testItem;
  private final String userId = "user123";
  private final String itemId = "item123";

  @BeforeEach
  void setUp() {
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
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  @Test
  void save_shouldDelegateToMongoRepository() {
    // Given
    when(mongoItemRepository.save(any(Item.class))).thenReturn(testItem);

    // When
    Item result = itemRepository.save(testItem);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(itemId);
    verify(mongoItemRepository).save(testItem);
  }

  @Test
  void findById_shouldDelegateToMongoRepository() {
    // Given
    when(mongoItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

    // When
    Optional<Item> result = itemRepository.findById(itemId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(itemId);
    verify(mongoItemRepository).findById(itemId);
  }

  @Test
  void findById_shouldReturnEmpty_whenItemNotFound() {
    // Given
    when(mongoItemRepository.findById(itemId)).thenReturn(Optional.empty());

    // When
    Optional<Item> result = itemRepository.findById(itemId);

    // Then
    assertThat(result).isEmpty();
    verify(mongoItemRepository).findById(itemId);
  }

  @Test
  void findAllByUserId_shouldDelegateToMongoRepository() {
    // Given
    Item item1 = Item.builder().id("item1").userId(userId).name("Item 1")
        .category(ItemCategory.TOPS).build();
    Item item2 = Item.builder().id("item2").userId(userId).name("Item 2")
        .category(ItemCategory.BOTTOMS).build();
    List<Item> items = List.of(item1, item2);

    when(mongoItemRepository.findAllByUserId(userId)).thenReturn(items);

    // When
    List<Item> result = itemRepository.findAllByUserId(userId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(item1, item2);
    verify(mongoItemRepository).findAllByUserId(userId);
  }

  @Test
  void findByUserIdAndCategory_shouldDelegateToMongoRepository() {
    // Given
    ItemCategory category = ItemCategory.TOPS;
    Item item1 = Item.builder().id("item1").userId(userId).name("T-Shirt").category(category)
        .build();
    List<Item> items = List.of(item1);

    when(mongoItemRepository.findByUserIdAndCategory(userId, category)).thenReturn(items);

    // When
    List<Item> result = itemRepository.findByUserIdAndCategory(userId, category);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).category()).isEqualTo(category);
    verify(mongoItemRepository).findByUserIdAndCategory(userId, category);
  }

  @Test
  void deleteById_shouldDelegateToMongoRepository() {
    // When
    itemRepository.deleteById(itemId);

    // Then
    verify(mongoItemRepository).deleteById(itemId);
  }
}

