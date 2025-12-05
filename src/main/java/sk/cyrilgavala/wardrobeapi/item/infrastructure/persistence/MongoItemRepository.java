package sk.cyrilgavala.wardrobeapi.item.infrastructure.persistence;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;

public interface MongoItemRepository extends MongoRepository<Item, String> {

  List<Item> findByUserIdAndCategory(String userId, ItemCategory category);

  List<Item> findAllByUserId(String userId);
}