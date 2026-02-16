package sk.cyrilgavala.wardrobeapi.item.domain.repository;

import java.util.List;
import java.util.Optional;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;

/**
 * Repository interface for {@link Item} domain aggregate. Provides persistence operations for
 * wardrobe items.
 *
 * <p>This interface follows the Repository pattern from Domain-Driven Design,
 * abstracting the underlying data access mechanism and allowing the domain layer to remain
 * independent of infrastructure concerns.
 */
public interface ItemRepository {

  /**
   * Persists an item to the database. If the item already exists (has an ID), it will be updated.
   *
   * @param item the item to save
   * @return the saved item with generated ID if it was new
   * @throws IllegalArgumentException if item is null
   */
  Item save(Item item);

  /**
   * Finds an item by its unique identifier.
   *
   * @param id the item ID
   * @return an Optional containing the item if found, empty otherwise
   * @throws IllegalArgumentException if id is null
   */
  Optional<Item> findById(String id);

  /**
   * Retrieves all items belonging to a specific user.
   *
   * @param userId the user's unique identifier
   * @return a list of items owned by the user, empty list if none found
   * @throws IllegalArgumentException if userId is null
   */
  List<Item> findAllByUserId(String userId);

  /**
   * Deletes an item by its unique identifier. Does nothing if the item doesn't exist.
   *
   * @param id the item ID to delete
   * @throws IllegalArgumentException if id is null
   */
  void deleteById(String id);

}

