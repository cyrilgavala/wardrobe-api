package sk.cyrilgavala.wardrobeapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sk.cyrilgavala.wardrobeapi.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByUsername(String username);
}
