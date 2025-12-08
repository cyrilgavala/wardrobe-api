package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;

/**
 * Mapper for {@link Item}.
 */
@Component
@AllArgsConstructor
public class ItemMapper {

  private final CategoryMapper categoryMapper;
  private final RoomMapper roomMapper;

  public Item fromCreateCommand(CreateItemCommand command) {
    return Item.create(
        command.userId(),
        command.name(),
        command.description(),
        categoryMapper.map(command.category()),
        roomMapper.map(command.room()),
        command.color(),
        command.brand(),
        command.size(),
        command.washingTemperature(),
        command.canBeIroned(),
        command.canBeTumbleDried(),
        command.canBeDryCleaned(),
        command.canBeBleached(),
        command.imageUrl()
    );
  }

  public Item fromUpdateCommand(Item existingItem, UpdateItemCommand command) {
    return existingItem.update(
        command.name(),
        command.description(),
        categoryMapper.map(command.category()),
        roomMapper.map(command.room()),
        command.color(),
        command.brand(),
        command.size(),
        command.washingTemperature(),
        command.canBeIroned(),
        command.canBeTumbleDried(),
        command.canBeDryCleaned(),
        command.canBeBleached(),
        command.imageUrl()
    );
  }
}
