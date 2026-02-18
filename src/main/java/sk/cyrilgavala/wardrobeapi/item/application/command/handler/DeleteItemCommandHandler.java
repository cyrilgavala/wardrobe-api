package sk.cyrilgavala.wardrobeapi.item.application.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.image.application.service.ImageStorageService;
import sk.cyrilgavala.wardrobeapi.item.application.command.DeleteItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

/**
 * Command handler for deleting wardrobe items.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteItemCommandHandler {

  private final ItemRepository itemRepository;
  private final ImageStorageService imageStorageService;

  @Transactional
  public void handle(DeleteItemCommand command) {
    log.info("Deleting item with id: {}", command.id());

    // Find existing item
    Item item = itemRepository.findById(command.id())
        .orElseThrow(() -> {
          log.warn("Delete failed: item not found - {}", command.id());
          return ItemNotFoundException.withId(command.id());
        });

    // Verify ownership
    if (!item.userId().equals(command.userId())) {
      log.warn("Delete failed: access denied to item {} for user {}", command.id(),
          command.userId());
      throw ItemAccessDeniedException.withId(command.id());
    }

    // Delete associated image if exists
    if (item.imageId() != null) {
      imageStorageService.deleteImage(item.imageId());
      log.info("Deleted associated image: {}", item.imageId());
    }

    itemRepository.deleteById(command.id());
    log.info("Item deleted successfully: {}", command.id());
  }
}

