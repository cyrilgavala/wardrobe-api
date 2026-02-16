package sk.cyrilgavala.wardrobeapi.item.application.command.handler;

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
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.ItemMapper;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class UpdateItemCommandHandlerTest {

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private ItemMapper itemMapper;

  @InjectMocks
  private UpdateItemCommandHandler handler;

  @Test
  void updatesExistingItemSuccessfully() {
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
    Item existingItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Old Jeans")
        .description("Old description")
        .build();
    Item updatedItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Updated Jeans")
        .description("New description")
        .color("Dark Blue")
        .brand("Levi's")
        .size("L")
        .washingTemperature(60)
        .canBeIroned(false)
        .canBeDried(true)
        .canBeBleached(false)
        .imageUrl("https://new-image.jpg")
        .boxNumber(7)
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(existingItem));
    when(itemMapper.fromUpdateCommand(existingItem, command)).thenReturn(updatedItem);
    when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

    Item result = handler.handle(command);

    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("Updated Jeans");
    assertThat(result.description()).isEqualTo("New description");
    verify(itemRepository).save(updatedItem);
  }

  @Test
  void throwsExceptionWhenItemNotFound() {
    UpdateItemCommand command = new UpdateItemCommand(
        "nonexistent",
        "user123",
        "Name",
        "Desc",
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

    when(itemRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(command))
        .isInstanceOf(ItemNotFoundException.class);
  }

  @Test
  void throwsExceptionWhenUserDoesNotOwnItem() {
    UpdateItemCommand command = new UpdateItemCommand(
        "item123",
        "user456",
        "Name",
        "Desc",
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
    Item existingItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Jeans")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(existingItem));

    assertThatThrownBy(() -> handler.handle(command))
        .isInstanceOf(ItemAccessDeniedException.class);
  }

  @Test
  void updatesOnlyChangedFields() {
    UpdateItemCommand command = new UpdateItemCommand(
        "item123",
        "user123",
        "New Name",
        "Old description",
        "Blue",
        "Levi's",
        "M",
        40,
        true,
        false,
        false,
        null,
        5
    );
    Item existingItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Old Name")
        .description("Old description")
        .build();
    Item updatedItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("New Name")
        .description("Old description")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(existingItem));
    when(itemMapper.fromUpdateCommand(existingItem, command)).thenReturn(updatedItem);
    when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

    Item result = handler.handle(command);

    assertThat(result.name()).isEqualTo("New Name");
    assertThat(result.userId()).isEqualTo("user123");
  }

  @Test
  void preservesItemIdAndUserIdAfterUpdate() {
    UpdateItemCommand command = new UpdateItemCommand(
        "item123",
        "user123",
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
    Item existingItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Original")
        .build();
    Item updatedItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Updated Item")
        .build();

    when(itemRepository.findById("item123")).thenReturn(Optional.of(existingItem));
    when(itemMapper.fromUpdateCommand(existingItem, command)).thenReturn(updatedItem);
    when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

    Item result = handler.handle(command);

    assertThat(result).isEqualTo(updatedItem);
  }
}

