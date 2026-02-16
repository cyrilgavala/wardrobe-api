package sk.cyrilgavala.wardrobeapi.item.application.command.handler;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.item.application.command.DeleteItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class DeleteItemCommandHandlerTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private DeleteItemCommandHandler handler;

  @Test
  void deletesExistingItemSuccessfully() {
    DeleteItemCommand command = new DeleteItemCommand("item123", "user123");
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Jeans")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(item));

    handler.handle(command);

    verify(itemRepository).deleteById("item123");
  }

  @Test
  void throwsExceptionWhenItemNotFound() {
    DeleteItemCommand command = new DeleteItemCommand("nonexistent", "user123");

    when(itemRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(command))
        .isInstanceOf(ItemNotFoundException.class);
  }

  @Test
  void throwsExceptionWhenUserDoesNotOwnItem() {
    DeleteItemCommand command = new DeleteItemCommand("item123", "user456");
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Jeans")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(item));

    assertThatThrownBy(() -> handler.handle(command))
        .isInstanceOf(ItemAccessDeniedException.class);
  }

  @Test
  void doesNotDeleteWhenAccessDenied() {
    DeleteItemCommand command = new DeleteItemCommand("item123", "wrongUser");
    Item item = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Jeans")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(item));

    try {
      handler.handle(command);
    } catch (ItemAccessDeniedException e) {
      // Expected
    }

    verify(itemRepository, never()).deleteById("item123");
  }

  @Test
  void checksOwnershipBeforeDeletion() {
    DeleteItemCommand command = new DeleteItemCommand("item789", "user789");
    Item item = Item.builder()
        .id("item789")
        .userId("user789")
        .name("Shirt")
        .build();

    when(itemRepository.findById("item789")).thenReturn(Optional.of(item));

    handler.handle(command);

    verify(itemRepository).findById("item789");
    verify(itemRepository).deleteById("item789");
  }
}

