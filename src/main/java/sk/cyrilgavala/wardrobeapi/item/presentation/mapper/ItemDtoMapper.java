package sk.cyrilgavala.wardrobeapi.item.presentation.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.CreateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.ItemResponse;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.UpdateItemRequest;

@Component
public class ItemDtoMapper {

  public CreateItemCommand toCreateCommand(CreateItemRequest request, String userId) {
    return new CreateItemCommand(
        userId,
        request.name(),
        request.description(),
        request.category(),
        request.room(),
        request.color(),
        request.brand(),
        request.size(),
        request.washingTemperature(),
        request.canBeIroned(),
        request.canBeTumbleDried(),
        request.canBeDryCleaned(),
        request.canBeBleached(),
        request.imageUrl(),
        request.boxNumber()
    );
  }

  public UpdateItemCommand toUpdateCommand(UpdateItemRequest request, String id, String userId) {
    return new UpdateItemCommand(
        id,
        userId,
        request.name(),
        request.description(),
        request.category(),
        request.room(),
        request.color(),
        request.brand(),
        request.size(),
        request.washingTemperature(),
        request.canBeIroned(),
        request.canBeTumbleDried(),
        request.canBeDryCleaned(),
        request.canBeBleached(),
        request.imageUrl(),
        request.boxNumber()
    );
  }

  public ItemResponse toResponse(Item item) {
    if (item == null) {
      return null;
    }

    return ItemResponse.of(
        item.id(),
        item.userId(),
        item.name(),
        item.description(),
        item.category().name(),
        item.room().name(),
        item.color(),
        item.brand(),
        item.size(),
        item.washingTemperature(),
        item.canBeIroned(),
        item.canBeTumbleDried(),
        item.canBeDryCleaned(),
        item.canBeBleached(),
        item.imageUrl(),
        item.boxNumber(),
        item.createdAt(),
        item.updatedAt()
    );
  }

  public List<ItemResponse> toResponseList(List<Item> items) {
    if (items == null) {
      return List.of();
    }

    return items.stream()
        .map(this::toResponse)
        .toList();
  }
}
