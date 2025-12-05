package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.application.dto.ItemDto;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;

/**
 * Mapper for converting between Item domain entity and ItemDto. This mapper is used within the
 * application layer to convert domain entities to DTOs.
 */
@Component
@RequiredArgsConstructor
public class ItemDtoMapper {

  private final CategoryDtoMapper categoryMapper;
  private final RoomDtoMapper roomMapper;

  public ItemDto toDto(Item item) {
    if (item == null) {
      return null;
    }

    return ItemDto.builder()
        .id(item.id())
        .userId(item.userId())
        .name(item.name())
        .description(item.description())
        .category(categoryMapper.map(item.category()))
        .room(roomMapper.map(item.room()))
        .color(item.color())
        .brand(item.brand())
        .size(item.size())
        .washingTemperature(item.washingTemperature())
        .canBeIroned(item.canBeIroned())
        .canBeTumbleDried(item.canBeTumbleDried())
        .canBeDryCleaned(item.canBeDryCleaned())
        .canBeBleached(item.canBeBleached())
        .imageUrl(item.imageUrl())
        .createdAt(item.createdAt())
        .updatedAt(item.updatedAt())
        .build();
  }

  public List<ItemDto> toDtoList(List<Item> items) {
    if (items == null) {
      return List.of();
    }

    return items.stream()
        .map(this::toDto)
        .toList();
  }
}

