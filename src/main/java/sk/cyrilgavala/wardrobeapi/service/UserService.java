package sk.cyrilgavala.wardrobeapi.service;

import sk.cyrilgavala.wardrobeapi.web.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.UserResponse;

public interface UserService {

	UserResponse getByUsername(String username);

	void saveUser(RegisterRequest request);
}
