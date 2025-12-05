package sk.cyrilgavala.wardrobeapi.item.domain.repository;

import java.util.List;
import java.util.Optional;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;

public interface ItemRepository {

  Item save(Item item);

  Optional<Item> findById(String id);

  List<Item> findAllByUserId(String userId);

  List<Item> findByUserIdAndCategory(String userId, String category);

  void deleteById(String id);

}

