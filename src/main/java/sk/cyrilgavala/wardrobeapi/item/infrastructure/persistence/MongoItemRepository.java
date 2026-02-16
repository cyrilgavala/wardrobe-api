package sk.cyrilgavala.wardrobeapi.item.infrastructure.persistence;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;

/**
 * MongoDB repository interface for {@link Item} persistence. Extends Spring Data MongoDB's
 * {@link MongoRepository} to provide standard CRUD operations and custom query methods.
 *
 * <p>This is the infrastructure layer implementation that bridges
 * the domain repository interface with MongoDB-specific operations. Spring Data MongoDB
 * automatically implements this interface at runtime.
 */
public interface MongoItemRepository extends MongoRepository<Item, String> {

  /**
   * Finds all wardrobe items belonging to a specific user. Uses Spring Data's query derivation
   * mechanism to automatically generate the MongoDB query from the method name.
   *
   * @param userId the unique identifier of the user who owns the items
   * @return a list of all items owned by the user, empty list if none found
   */
  List<Item> findAllByUserId(String userId);
}