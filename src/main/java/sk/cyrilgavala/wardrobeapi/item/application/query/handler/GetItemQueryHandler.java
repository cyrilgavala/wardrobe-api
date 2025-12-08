package sk.cyrilgavala.wardrobeapi.item.application.query.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetItemQuery;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

/**
 * Query handler for retrieving a single wardrobe item.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetItemQueryHandler {

  private final ItemRepository itemRepository;

  @Transactional(readOnly = true)
  public Item handle(GetItemQuery query) {
    log.info("Fetching item with id: {}", query.id());

    Item item = itemRepository.findById(query.id())
        .orElseThrow(() -> {
          log.warn("Get failed: item not found - {}", query.id());
          return ItemNotFoundException.withId(query.id());
        });

    // Verify ownership
    if (!item.userId().equals(query.userId())) {
      log.warn("Get failed: access denied to item {} for user {}", query.id(), query.userId());
      throw ItemAccessDeniedException.withId(query.id());
    }

    return item;
  }
}

