package sk.cyrilgavala.wardrobeapi.shared.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mongodb.MongoDBContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  @ServiceConnection
  public MongoDBContainer mongoDBContainer() {
    try (var mongoDbContainer = new MongoDBContainer("mongo:7.0").withReplicaSet()) {
      mongoDbContainer.start();
      return mongoDbContainer;
    }
  }
}
