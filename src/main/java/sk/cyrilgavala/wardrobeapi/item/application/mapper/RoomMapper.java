package sk.cyrilgavala.wardrobeapi.item.application.mapper;

import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Room;

/**
 * Mapper for {@link Room}.
 */
@Component
public class RoomMapper {

  public String map(Room room) {
    return switch (room) {
      case BEDROOM -> "BEDROOM";
      case WARDROBE -> "WARDROBE";
      case CLOSET -> "CLOSET";
      case BATHROOM -> "BATHROOM";
      case LAUNDRY_ROOM -> "LAUNDRY_ROOM";
      case HALLWAY -> "HALLWAY";
      case GARAGE -> "GARAGE";
      case STORAGE -> "STORAGE";
      case OTHER -> "OTHER";
      default -> throw new IllegalArgumentException("Unexpected value: " + room);
    };
  }

  public Room map(String room) {
    return switch (room) {
      case "BEDROOM" -> Room.BEDROOM;
      case "WARDROBE" -> Room.WARDROBE;
      case "CLOSET" -> Room.CLOSET;
      case "BATHROOM" -> Room.BATHROOM;
      case "LAUNDRY_ROOM" -> Room.LAUNDRY_ROOM;
      case "HALLWAY" -> Room.HALLWAY;
      case "GARAGE" -> Room.GARAGE;
      case "STORAGE" -> Room.STORAGE;
      case "OTHER" -> Room.OTHER;
      default -> throw new IllegalArgumentException("Unexpected value: " + room);
    };
  }

}
