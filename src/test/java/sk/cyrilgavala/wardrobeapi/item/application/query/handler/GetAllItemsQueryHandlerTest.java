package sk.cyrilgavala.wardrobeapi.item.application.query.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetAllItemsQuery;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class GetAllItemsQueryHandlerTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private GetAllItemsQueryHandler handler;

  @Test
  void retrievesAllItemsForUser() {
    GetAllItemsQuery query = new GetAllItemsQuery("user123");
    List<Item> items = Arrays.asList(
        Item.builder().id("item1").userId("user123").name("Jeans").build(),
        Item.builder().id("item2").userId("user123").name("Shirt").build(),
        Item.builder().id("item3").userId("user123").name("Dress").build()
    );

    when(itemRepository.findAllByUserId("user123")).thenReturn(items);

    List<Item> result = handler.handle(query);

    assertThat(result).hasSize(3);
    assertThat(result).extracting("name").containsExactly("Jeans", "Shirt", "Dress");
    verify(itemRepository).findAllByUserId("user123");
  }

  @Test
  void returnsEmptyListWhenUserHasNoItems() {
    GetAllItemsQuery query = new GetAllItemsQuery("user456");

    when(itemRepository.findAllByUserId("user456")).thenReturn(Collections.emptyList());

    List<Item> result = handler.handle(query);

    assertThat(result).isEmpty();
  }

  @Test
  void retrievesSingleItemWhenUserHasOne() {
    GetAllItemsQuery query = new GetAllItemsQuery("user789");
    List<Item> items = Collections.singletonList(
        Item.builder().id("item1").userId("user789").name("Jacket").build()
    );

    when(itemRepository.findAllByUserId("user789")).thenReturn(items);

    List<Item> result = handler.handle(query);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("Jacket");
  }

  @Test
  void retrievesOnlyItemsForSpecificUser() {
    GetAllItemsQuery query = new GetAllItemsQuery("user123");
    List<Item> items = Arrays.asList(
        Item.builder().id("item1").userId("user123").name("Item1").build(),
        Item.builder().id("item2").userId("user123").name("Item2").build()
    );

    when(itemRepository.findAllByUserId("user123")).thenReturn(items);

    List<Item> result = handler.handle(query);

    assertThat(result).allMatch(item -> item.userId().equals("user123"));
  }

  @Test
  void retrievesMultipleItemsWithDifferentAttributes() {
    GetAllItemsQuery query = new GetAllItemsQuery("user999");
    List<Item> items = Arrays.asList(
        Item.builder()
            .id("item1")
            .userId("user999")
            .name("Jeans")
            .color("Blue")
            .brand("Levi's")
            .build(),
        Item.builder()
            .id("item2")
            .userId("user999")
            .name("T-Shirt")
            .color("White")
            .brand("Nike")
            .build(),
        Item.builder()
            .id("item3")
            .userId("user999")
            .name("Dress")
            .color("Red")
            .brand("Zara")
            .build()
    );

    when(itemRepository.findAllByUserId("user999")).thenReturn(items);

    List<Item> result = handler.handle(query);

    assertThat(result).containsExactlyElementsOf(items);
  }
}

