package sk.cyrilgavala.wardrobeapi.item.application.query.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetItemQuery;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class GetItemQueryHandlerTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private GetItemQueryHandler handler;

  @Test
  void retrievesItemSuccessfully() {
    GetItemQuery query = new GetItemQuery("item123", "user123");
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Blue Jeans")
        .description("Comfortable denim")
        .color("Blue")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(item));

    Item result = handler.handle(query);

    assertThat(result).isEqualTo(item);
    verify(itemRepository).findById("item123");
  }

  @Test
  void throwsExceptionWhenItemNotFound() {
    GetItemQuery query = new GetItemQuery("nonexistent", "user123");

    when(itemRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(query))
        .isInstanceOf(ItemNotFoundException.class);
  }

  @Test
  void throwsExceptionWhenUserDoesNotOwnItem() {
    GetItemQuery query = new GetItemQuery("item123", "user456");
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Jeans")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(item));

    assertThatThrownBy(() -> handler.handle(query))
        .isInstanceOf(ItemAccessDeniedException.class);
  }

  @Test
  void returnsItemWithAllFields() {
    GetItemQuery query = new GetItemQuery("item456", "user456");
    Item item = Item.builder()
        .id("item456")
        .userId("user456")
        .name("T-Shirt")
        .description("Cotton shirt")
        .color("White")
        .brand("Nike")
        .size("M")
        .washingTemperature(30)
        .canBeIroned(true)
        .canBeDried(false)
        .canBeBleached(false)
        .imageUrl("https://example.com/tshirt.jpg")
        .boxNumber(3)
        .build();

    when(itemRepository.findById("item456")).thenReturn(Optional.of(item));

    Item result = handler.handle(query);

    assertThat(result).isEqualTo(item);
  }

  @Test
  void verifiesOwnershipBeforeReturningItem() {
    GetItemQuery query = new GetItemQuery("item789", "user789");
    Item item = Item.builder()
        .id("item789")
        .userId("user789")
        .name("Dress")
        .build();

    when(itemRepository.findById("item789")).thenReturn(Optional.of(item));

    Item result = handler.handle(query);

    assertThat(result).isEqualTo(item);
  }
}

