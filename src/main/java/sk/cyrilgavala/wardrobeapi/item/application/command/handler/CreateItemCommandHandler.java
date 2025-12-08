package sk.cyrilgavala.wardrobeapi.item.application.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.ItemMapper;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

/**
 * Command handler for creating new wardrobe items.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateItemCommandHandler {

  private final ItemRepository itemRepository;
  private final ItemMapper itemMapper;

  @Transactional
  public Item handle(CreateItemCommand command) {
    log.info("Creating new item for user: {}", command.userId());

    Item item = itemMapper.fromCreateCommand(command);

    Item savedItem = itemRepository.save(item);
    log.info("Item created successfully with id: {}", savedItem.id());

    return savedItem;
  }
}

