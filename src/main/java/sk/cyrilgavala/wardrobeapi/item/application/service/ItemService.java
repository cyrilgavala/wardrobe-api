package sk.cyrilgavala.wardrobeapi.item.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;

  @Transactional
  public Item createItem(CreateItemCommand command) {
    log.info("Creating new item for user: {}", command.userId());

    Item item = Item.create(
        command.userId(),
        command.name(),
        command.description(),
        command.category(),
        command.color(),
        command.brand(),
        command.size(),
        command.washingTemperature(),
        command.canBeIroned(),
        command.canBeTumbleDried(),
        command.canBeDryCleaned(),
        command.canBeBleached(),
        command.imageUrl()
    );

    Item savedItem = itemRepository.save(item);
    log.info("Item created successfully with id: {}", savedItem.id());

    return savedItem;
  }

  @Transactional
  public Item updateItem(UpdateItemCommand command) {
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
    Item updatedItem = existingItem.update(
        command.name(),
        command.description(),
        command.category(),
        command.color(),
        command.brand(),
        command.size(),
        command.washingTemperature(),
        command.canBeIroned(),
        command.canBeTumbleDried(),
        command.canBeDryCleaned(),
        command.canBeBleached(),
        command.imageUrl()
    );

    Item savedItem = itemRepository.save(updatedItem);
    log.info("Item updated successfully: {}", savedItem.id());

    return savedItem;
  }

  @Transactional
  public void deleteItem(String id, String userId) {
    log.info("Deleting item with id: {}", id);

    // Find existing item
    Item item = itemRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Delete failed: item not found - {}", id);
          return ItemNotFoundException.withId(id);
        });

    // Verify ownership
    if (!item.userId().equals(userId)) {
      log.warn("Delete failed: access denied to item {} for user {}", id, userId);
      throw ItemAccessDeniedException.withId(id);
    }

    itemRepository.deleteById(id);
    log.info("Item deleted successfully: {}", id);
  }

  @Transactional(readOnly = true)
  public Item getItem(String id, String userId) {
    log.info("Fetching item with id: {}", id);

    Item item = itemRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Get failed: item not found - {}", id);
          return ItemNotFoundException.withId(id);
        });

    // Verify ownership
    if (!item.userId().equals(userId)) {
      log.warn("Get failed: access denied to item {} for user {}", id, userId);
      throw ItemAccessDeniedException.withId(id);
    }

    return item;
  }

  @Transactional(readOnly = true)
  public List<Item> getAllItemsForUser(String userId) {
    log.info("Fetching all items for user: {}", userId);
    return itemRepository.findAllByUserId(userId);
  }

  @Transactional(readOnly = true)
  public List<Item> getItemsByCategory(String userId, ItemCategory category) {
    log.info("Fetching items for user {} with category: {}", userId, category);
    return itemRepository.findByUserIdAndCategory(userId, category);
  }
}

