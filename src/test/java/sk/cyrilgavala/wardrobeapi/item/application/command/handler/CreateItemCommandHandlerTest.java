package sk.cyrilgavala.wardrobeapi.item.application.command.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.ItemMapper;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class CreateItemCommandHandlerTest {

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private ItemMapper itemMapper;

  @InjectMocks
  private CreateItemCommandHandler handler;

  @Test
  void createsItemWithAllFields() {
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
    Item mappedItem = Item.create(
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
    Item savedItem = Item.builder()
        .id("item123")
        .userId("user123")
        .name("Blue Jeans")
        .description("Comfortable denim jeans")
        .color("Blue")
        .brand("Levi's")
        .size("M")
        .washingTemperature(40)
        .canBeIroned(true)
        .canBeDried(false)
        .canBeBleached(false)
        .imageUrl("https://example.com/image.jpg")
        .boxNumber(5)
        .build();

    when(itemMapper.fromCreateCommand(command)).thenReturn(mappedItem);
    when(itemRepository.save(mappedItem)).thenReturn(savedItem);

    Item result = handler.handle(command);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo("item123");
    assertThat(result.userId()).isEqualTo("user123");
    assertThat(result.name()).isEqualTo("Blue Jeans");
    verify(itemMapper).fromCreateCommand(command);
    verify(itemRepository).save(mappedItem);
  }

  @Test
  void createsItemWithMinimalFields() {
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
    Item mappedItem = Item.create(
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
    Item savedItem = Item.builder()
        .id("item456")
        .userId("user456")
        .name("T-Shirt")
        .build();

    when(itemMapper.fromCreateCommand(command)).thenReturn(mappedItem);
    when(itemRepository.save(mappedItem)).thenReturn(savedItem);

    Item result = handler.handle(command);

    assertThat(result).isEqualTo(savedItem);
  }

  @Test
  void savesItemToRepository() {
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
    Item mappedItem = Item.create(
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
    Item savedItem = Item.builder().id("item789").userId("user789").build();

    when(itemMapper.fromCreateCommand(command)).thenReturn(mappedItem);
    when(itemRepository.save(mappedItem)).thenReturn(savedItem);

    handler.handle(command);

    verify(itemRepository).save(mappedItem);
  }
}

