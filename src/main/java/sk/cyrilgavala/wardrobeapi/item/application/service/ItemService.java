package sk.cyrilgavala.wardrobeapi.item.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.dto.ItemDto;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.CategoryMapper;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.ItemDtoMapper;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.RoomMapper;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

  private final ItemRepository itemRepository;
  private final ItemDtoMapper itemDtoMapper;
  private final CategoryMapper categoryMapper;
  private final RoomMapper roomMapper;

  @Transactional
  public ItemDto createItem(CreateItemCommand command) {
    log.info("Creating new item for user: {}", command.userId());

    Item item = Item.create(
        command.userId(),
        command.name(),
        command.description(),
        categoryMapper.fromString(command.category()),
        roomMapper.fromString(command.room()),
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

    return itemDtoMapper.toDto(savedItem);
  }

  @Transactional
  public ItemDto updateItem(UpdateItemCommand command) {
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
        categoryMapper.fromString(command.category()),
        roomMapper.fromString(command.room()),
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

    return itemDtoMapper.toDto(savedItem);
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
  public ItemDto getItem(String id, String userId) {
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

    return itemDtoMapper.toDto(item);
  }

  @Transactional(readOnly = true)
  public List<ItemDto> getAllItemsForUser(String userId) {
    log.info("Fetching all items for user: {}", userId);
    List<Item> items = itemRepository.findAllByUserId(userId);
    return itemDtoMapper.toDtoList(items);
  }

  @Transactional(readOnly = true)
  public List<ItemDto> getItemsByCategory(String userId, String category) {
    log.info("Fetching items for user {} with category: {}", userId, category);
    List<Item> items = itemRepository.findByUserIdAndCategory(userId, category);
    return itemDtoMapper.toDtoList(items);
  }
}

