package sk.cyrilgavala.wardrobeapi.item.domain.repository;

import java.util.List;
import java.util.Optional;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;

public interface ItemRepository {

  Item save(Item item);

  Optional<Item> findById(String id);

  List<Item> findAllByUserId(String userId);

  List<Item> findByUserIdAndCategory(String userId, ItemCategory category);

  void deleteById(String id);

}

