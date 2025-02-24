package sk.cyrilgavala.wardrobeapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sk.cyrilgavala.wardrobeapi.mapper.UserMapper;
import sk.cyrilgavala.wardrobeapi.model.User;
import sk.cyrilgavala.wardrobeapi.repository.UserRepository;
import sk.cyrilgavala.wardrobeapi.service.SecurityService;
import sk.cyrilgavala.wardrobeapi.service.UserService;
import sk.cyrilgavala.wardrobeapi.web.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.UserResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final SecurityService securityService;

	@Override
	public UserResponse getByUsername(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		return userMapper.modelToResponse(user);
	}

	@Override
	public void saveUser(RegisterRequest request) {
		User user = userMapper.registerRequestToModel(request);
		user.setPassword(securityService.encryptPassword(request.getPassword()));
		userRepository.save(user);
	}

}
