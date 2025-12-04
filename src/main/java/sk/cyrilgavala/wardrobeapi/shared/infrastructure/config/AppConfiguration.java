package sk.cyrilgavala.wardrobeapi.shared.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMongoRepositories(basePackages = "sk.cyrilgavala.wardrobeapi.**.persistence")
@ComponentScan({
    "sk.cyrilgavala.wardrobeapi"
})
public class AppConfiguration {

}
