package sk.cyrilgavala.wardrobeapi.item.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;

@ExtendWith(MockitoExtension.class)
class ItemRepositoryImplTest {

  @Mock
  private MongoItemRepository mongoItemRepository;

  @InjectMocks
  private ItemRepositoryImpl repository;

  @Test
  void savesItemSuccessfully() {
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Jeans")
        .build();

    when(mongoItemRepository.save(item)).thenReturn(item);

    Item result = repository.save(item);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo("item123");
    verify(mongoItemRepository).save(item);
  }

  @Test
  void savesNewItemWithoutId() {
    Item newItem = Item.builder()
        .userId("user456")
        .name("T-Shirt")
        .build();
    Item savedItem = Item.builder()
        .id("generatedId")
        .userId("user456")
        .name("T-Shirt")
        .build();

    when(mongoItemRepository.save(newItem)).thenReturn(savedItem);

    Item result = repository.save(newItem);

    assertThat(result.id()).isEqualTo("generatedId");
  }

  @Test
  void findsItemByIdSuccessfully() {
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Dress")
        .build();

    when(mongoItemRepository.findById("item123")).thenReturn(Optional.of(item));

    Optional<Item> result = repository.findById("item123");

    assertThat(result).contains(item);
    verify(mongoItemRepository).findById("item123");
  }

  @Test
  void returnsEmptyWhenItemNotFoundById() {
    when(mongoItemRepository.findById("nonexistent")).thenReturn(Optional.empty());

    Optional<Item> result = repository.findById("nonexistent");

    assertThat(result).isEmpty();
  }

  @Test
  void findsAllItemsByUserId() {
    List<Item> items = Arrays.asList(
        Item.builder().id("item1").userId("user123").name("Jeans").build(),
        Item.builder().id("item2").userId("user123").name("Shirt").build(),
        Item.builder().id("item3").userId("user123").name("Dress").build()
    );

    when(mongoItemRepository.findAllByUserId("user123")).thenReturn(items);

    List<Item> result = repository.findAllByUserId("user123");

    assertThat(result).containsExactlyElementsOf(items);
    verify(mongoItemRepository).findAllByUserId("user123");
  }

  @Test
  void returnsEmptyListWhenUserHasNoItems() {
    when(mongoItemRepository.findAllByUserId("user456")).thenReturn(Collections.emptyList());

    List<Item> result = repository.findAllByUserId("user456");

    assertThat(result).isEmpty();
  }

  @Test
  void deletesItemById() {
    repository.deleteById("item123");

    verify(mongoItemRepository).deleteById("item123");
  }

  @Test
  void delegatesSaveToMongoRepository() {
    Item item = Item.builder()
        .id("item789")
        .userId("user789")
        .name("Jacket")
        .color("Black")
        .brand("Nike")
        .build();

    when(mongoItemRepository.save(item)).thenReturn(item);

    repository.save(item);

    verify(mongoItemRepository).save(item);
  }

  @Test
  void delegatesFindByIdToMongoRepository() {
    when(mongoItemRepository.findById("item999")).thenReturn(Optional.empty());

    repository.findById("item999");

    verify(mongoItemRepository).findById("item999");
  }

  @Test
  void delegatesFindAllByUserIdToMongoRepository() {
    List<Item> items = Collections.singletonList(
        Item.builder().id("item1").userId("user999").build()
    );
    when(mongoItemRepository.findAllByUserId("user999")).thenReturn(items);

    repository.findAllByUserId("user999");

    verify(mongoItemRepository).findAllByUserId("user999");
  }

  @Test
  void delegatesDeleteByIdToMongoRepository() {
    repository.deleteById("item888");

    verify(mongoItemRepository).deleteById("item888");
  }
}

