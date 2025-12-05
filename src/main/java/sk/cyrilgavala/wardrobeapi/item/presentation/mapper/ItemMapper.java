package sk.cyrilgavala.wardrobeapi.item.presentation.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.dto.ItemDto;
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
        request.room(),
        request.color(),
        request.brand(),
        request.size(),
        request.washingTemperature(),
        request.canBeIroned(),
        request.canBeTumbleDried(),
        request.canBeDryCleaned(),
        request.canBeBleached(), request.imageUrl(), request.boxNumber()
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
        request.canBeBleached(), request.imageUrl(), request.boxNumber()
    );
  }

  public ItemResponse toResponse(ItemDto itemDto) {
    if (itemDto == null) {
      return null;
    }

    return ItemResponse.of(
        itemDto.id(),
        itemDto.userId(),
        itemDto.name(),
        itemDto.description(),
        itemDto.category(),
        itemDto.room(),
        itemDto.color(),
        itemDto.brand(),
        itemDto.size(),
        itemDto.washingTemperature(),
        itemDto.canBeIroned(),
        itemDto.canBeTumbleDried(),
        itemDto.canBeDryCleaned(),
        itemDto.canBeBleached(),
        itemDto.imageUrl(), itemDto.boxNumber(),
        itemDto.createdAt(),
        itemDto.updatedAt()
    );
  }

  public List<ItemResponse> toResponseList(List<ItemDto> itemDtos) {
    if (itemDtos == null) {
      return List.of();
    }

    return itemDtos.stream()
        .map(this::toResponse)
        .toList();
  }
}
