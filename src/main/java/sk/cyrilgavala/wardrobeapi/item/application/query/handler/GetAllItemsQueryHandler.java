package sk.cyrilgavala.wardrobeapi.item.application.query.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetAllItemsQuery;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

/**
 * Query handler for retrieving all wardrobe items for a user.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllItemsQueryHandler {

  private final ItemRepository itemRepository;

  @Transactional(readOnly = true)
  public List<Item> handle(GetAllItemsQuery query) {
    log.info("Fetching all items for user: {}", query.userId());
    return itemRepository.findAllByUserId(query.userId());
  }
}

