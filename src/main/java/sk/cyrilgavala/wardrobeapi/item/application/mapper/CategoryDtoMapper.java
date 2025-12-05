package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Category;

@Component
public class CategoryDtoMapper {

  public String map(Category category) {
    return category != null ? category.name() : null;
  }

}

