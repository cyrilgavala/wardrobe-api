package sk.cyrilgavala.wardrobeapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sk.cyrilgavala.wardrobeapi.model.User;
import sk.cyrilgavala.wardrobeapi.web.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserResponse modelToResponse(User model);

	@Mapping(target = "id", ignore = true)
	User registerRequestToModel(RegisterRequest request);
}
