package sk.cyrilgavala.wardrobeapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import sk.cyrilgavala.wardrobeapi.model.User;

public interface UserRepository extends MongoRepository<User, String> {

	Optional<User> findByUsername(String username);
}
