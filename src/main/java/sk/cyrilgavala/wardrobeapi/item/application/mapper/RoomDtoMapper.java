package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Room;

@Component
public class RoomDtoMapper {

  public String map(Room room) {
    return room != null ? room.name() : null;
  }

}
