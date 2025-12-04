package sk.cyrilgavala.wardrobeapi.auth.domain.repository;

import java.util.Optional;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;

public interface UserRepository {

  User save(User user);

  Optional<User> findById(String id);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  void deleteById(String id);
}

