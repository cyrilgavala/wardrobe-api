package sk.cyrilgavala.wardrobeapi.item.application.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.ItemMapper;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

/**
 * Command handler for updating existing wardrobe items.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateItemCommandHandler {

  private final ItemRepository itemRepository;
  private final ItemMapper itemMapper;

  @Transactional
  public Item handle(UpdateItemCommand command) {
    log.info("Updating item with id: {}", command.id());

    // Find existing item
    Item existingItem = itemRepository.findById(command.id())
        .orElseThrow(() -> {
          log.warn("Update failed: item not found - {}", command.id());
          return ItemNotFoundException.withId(command.id());
        });

    // Verify ownership
    if (!existingItem.userId().equals(command.userId())) {
      log.warn("Update failed: access denied to item {} for user {}",
          command.id(), command.userId());
      throw ItemAccessDeniedException.withId(command.id());
    }

    // Update item
    Item updatedItem = itemMapper.fromUpdateCommand(existingItem, command);

    Item savedItem = itemRepository.save(updatedItem);
    log.info("Item updated successfully: {}", savedItem.id());

    return savedItem;
  }
}

