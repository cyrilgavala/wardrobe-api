package sk.cyrilgavala.wardrobeapi.auth.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;

@Repository
public interface MongoUserRepository extends MongoRepository<User, String> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
