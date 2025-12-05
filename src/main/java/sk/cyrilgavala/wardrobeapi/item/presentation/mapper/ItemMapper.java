package sk.cyrilgavala.wardrobeapi.item.presentation.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.CreateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.ItemResponse;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.UpdateItemRequest;

@Component
public class ItemMapper {

  public CreateItemCommand toCreateCommand(CreateItemRequest request, String userId) {
    return new CreateItemCommand(
        userId,
        request.name(),
        request.description(),
        request.category(),
        request.color(),
        request.brand(),
        request.size(),
        request.washingTemperature(),
        request.canBeIroned(),
        request.canBeTumbleDried(),
        request.canBeDryCleaned(),
        request.canBeBleached(),
        request.imageUrl()
    );
  }

  public UpdateItemCommand toUpdateCommand(UpdateItemRequest request, String id, String userId) {
    return new UpdateItemCommand(
        id,
        userId,
        request.name(),
        request.description(),
        request.category(),
        request.color(),
        request.brand(),
        request.size(),
        request.washingTemperature(),
        request.canBeIroned(),
        request.canBeTumbleDried(),
        request.canBeDryCleaned(),
        request.canBeBleached(),
        request.imageUrl()
    );
  }

  public ItemResponse toResponse(Item item) {
    return ItemResponse.of(
        item.id(),
        item.userId(),
        item.name(),
        item.description(),
        item.category(),
        item.color(),
        item.brand(),
        item.size(),
        item.washingTemperature(),
        item.canBeIroned(),
        item.canBeTumbleDried(),
        item.canBeDryCleaned(),
        item.canBeBleached(),
        item.imageUrl(),
        item.createdAt(),
        item.updatedAt()
    );
  }
}


