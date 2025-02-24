package sk.cyrilgavala.wardrobeapi.service.impl;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sk.cyrilgavala.wardrobeapi.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return mapToUserDetails(userRepository.findByUsername(username).orElseThrow());
	}

	private UserDetails mapToUserDetails(sk.cyrilgavala.wardrobeapi.model.User userResponse) {
		return new User(userResponse.getUsername(), userResponse.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(userResponse.getRole())));
	}
}
