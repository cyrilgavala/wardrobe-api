package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Category;

@Component
public class CategoryMapper {

  public String toString(Category category) {
    return category != null ? category.name() : null;
  }

  public Category fromString(String category) {
    if (category == null) {
      return null;
    }
    try {
      return Category.valueOf(category.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid category: " + category);
    }
  }
}

