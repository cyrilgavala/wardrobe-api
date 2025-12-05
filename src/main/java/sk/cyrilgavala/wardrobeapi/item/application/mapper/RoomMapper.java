package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Room;

@Component
public class RoomMapper {

  public String toString(Room room) {
    return room != null ? room.name() : null;
  }

  public Room fromString(String room) {
    if (room == null) {
      return null;
    }
    try {
      return Room.valueOf(room.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid room: " + room);
    }
  }
}
