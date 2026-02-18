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

  public Item fromCreateCommand(CreateItemCommand command) {
    return Item.create(
        command.userId(),
        command.name(),
        command.description(),
        command.color(),
        command.brand(),
        command.size(),
        command.washingTemperature(),
        command.canBeIroned(),
        command.canBeDried(),
        command.canBeBleached(),
        command.imageId(),
        command.boxNumber()
    );
  }

  public Item fromUpdateCommand(Item existingItem, UpdateItemCommand command) {
    return existingItem.update(
        command.name(),
        command.description(),
        command.color(),
        command.brand(),
        command.size(),
        command.washingTemperature(),
        command.canBeIroned(),
        command.canBeDried(),
        command.canBeBleached(),
        command.imageId(),
        command.boxNumber()
    );
  }
}
