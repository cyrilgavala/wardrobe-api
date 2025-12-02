package sk.cyrilgavala.wardrobeapi.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import sk.cyrilgavala.wardrobeapi.model.User;
import sk.cyrilgavala.wardrobeapi.web.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.UserResponse;

@Configuration
@EnableWebSecurity
@EnableMongoRepositories(basePackages = "sk.cyrilgavala.wardrobeapi.repository")
@ComponentScan({
    "sk.cyrilgavala.wardrobeapi.config", "sk.cyrilgavala.wardrobeapi.security",
    "sk.cyrilgavala.wardrobeapi.service", "sk.cyrilgavala.wardrobeapi.web"
})
public class AppConfiguration {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.createTypeMap(User.class, UserResponse.class);
    modelMapper.createTypeMap(RegisterRequest.class, User.class)
        .addMappings(mapper -> mapper.skip(User::setId));
    return modelMapper;
  }
}
