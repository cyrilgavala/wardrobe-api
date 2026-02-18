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

  public CreateItemCommand toCreateCommand(CreateItemRequest request, String userId,
      String imageId) {
    return new CreateItemCommand(
        userId,
        request.name(),
        request.description(),
        request.color(),
        request.brand(),
        request.size(),
        request.washingTemperature(),
        request.canBeIroned(),
        request.canBeDried(),
        request.canBeBleached(),
        imageId,
        request.boxNumber()
    );
  }

  public UpdateItemCommand toUpdateCommand(UpdateItemRequest request, String id, String userId,
      String imageId) {
    return new UpdateItemCommand(
        id,
        userId,
        request.name(),
        request.description(),
        request.color(),
        request.brand(),
        request.size(),
        request.washingTemperature(),
        request.canBeIroned(),
        request.canBeDried(),
        request.canBeBleached(),
        imageId,
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
        item.color(),
        item.brand(),
        item.size(),
        item.washingTemperature(),
        item.canBeIroned(),
        item.canBeDried(),
        item.canBeBleached(),
        item.imageId(),
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
