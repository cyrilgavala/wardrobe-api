package sk.cyrilgavala.wardrobeapi.auth.infrastructure.persistence;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.repository.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final MongoUserRepository mongoUserRepository;

  @Override
  public User save(User user) {
    return mongoUserRepository.save(user);
  }

  @Override
  public Optional<User> findById(String id) {
    return mongoUserRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return mongoUserRepository.findByUsername(username);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return mongoUserRepository.findByEmail(email);
  }

  @Override
  public boolean existsByUsername(String username) {
    return mongoUserRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return mongoUserRepository.existsByEmail(email);
  }

  @Override
  public void deleteById(String id) {
    mongoUserRepository.deleteById(id);
  }
}

