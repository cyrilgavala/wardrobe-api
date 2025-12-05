package sk.cyrilgavala.wardrobeapi.item.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sk.cyrilgavala.wardrobeapi.item.application.mapper.CategoryDtoMapper;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.repository.ItemRepository;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

  private final MongoItemRepository mongoItemRepository;
  private final CategoryDtoMapper categoryMapper;

  @Override
  public Item save(Item item) {
    return mongoItemRepository.save(item);
  }

  @Override
  public Optional<Item> findById(String id) {
    return mongoItemRepository.findById(id);
  }

  @Override
  public List<Item> findAllByUserId(String userId) {
    return mongoItemRepository.findAllByUserId(userId);
  }

  @Override
  public List<Item> findByUserIdAndCategory(String userId, String category) {
    return mongoItemRepository.findByUserIdAndCategory(userId, categoryMapper.fromString(category));
  }

  @Override
  public void deleteById(String id) {
    mongoItemRepository.deleteById(id);
  }

}

