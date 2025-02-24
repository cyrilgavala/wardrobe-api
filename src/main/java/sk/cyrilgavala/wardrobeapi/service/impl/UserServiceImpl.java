package sk.cyrilgavala.wardrobeapi.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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
	private final ModelMapper userMapper;
	private final SecurityService securityService;

	@Override
	public UserResponse getByUsername(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		return userMapper.map(user, UserResponse.class);
	}

	@Override
	public void saveUser(RegisterRequest request) {
		User user = userMapper.map(request, User.class);
		user.setPassword(securityService.encryptPassword(request.getPassword()));
		userRepository.save(user);
	}

}
