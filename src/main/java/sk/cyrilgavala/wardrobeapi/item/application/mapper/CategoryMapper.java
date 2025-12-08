package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Category;

/**
 * Mapper for {@link Category}.
 */
@Component
public class CategoryMapper {

  public String map(Category category) {
    return switch (category) {
      case TOPS -> "TOPS";
      case BOTTOMS -> "BOTTOMS";
      case DRESSES -> "DRESSES";
      case OUTERWEAR -> "OUTERWEAR";
      case SHOES -> "SHOES";
      case ACCESSORIES -> "ACCESSORIES";
      case UNDERWEAR -> "UNDERWEAR";
      case SPORTSWEAR -> "SPORTSWEAR";
      case SLEEPWEAR -> "SLEEPWEAR";
      case FORMAL -> "FORMAL";
      case OTHER -> "OTHER";
      default -> throw new IllegalArgumentException("Unexpected value: " + category);
    };
  }

  public Category map(String category) {
    return switch (category) {
      case "TOPS" -> Category.TOPS;
      case "BOTTOMS" -> Category.BOTTOMS;
      case "DRESSES" -> Category.DRESSES;
      case "OUTERWEAR" -> Category.OUTERWEAR;
      case "SHOES" -> Category.SHOES;
      case "ACCESSORIES" -> Category.ACCESSORIES;
      case "UNDERWEAR" -> Category.UNDERWEAR;
      case "SPORTSWEAR" -> Category.SPORTSWEAR;
      case "SLEEPWEAR" -> Category.SLEEPWEAR;
      case "FORMAL" -> Category.FORMAL;
      case "OTHER" -> Category.OTHER;
      default -> throw new IllegalArgumentException("Unexpected value: " + category);
    };
  }

}

