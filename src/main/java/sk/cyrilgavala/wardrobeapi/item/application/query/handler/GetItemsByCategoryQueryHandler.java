package sk.cyrilgavala.wardrobeapi.item.application.query.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.CategoryMapper;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetItemsByCategoryQuery;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

/**
 * Query handler for retrieving wardrobe items filtered by category.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetItemsByCategoryQueryHandler {

  private final ItemRepository itemRepository;
  private final CategoryMapper categoryMapper;

  @Transactional(readOnly = true)
  public List<Item> handle(GetItemsByCategoryQuery query) {
    log.info("Fetching items for user {} with category: {}", query.userId(), query.category());
    return itemRepository.findByUserIdAndCategory(query.userId(),
        categoryMapper.map(query.category()));
  }
}

