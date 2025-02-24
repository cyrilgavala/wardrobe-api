package sk.cyrilgavala.wardrobeapi.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableWebSecurity
@EnableJpaRepositories(basePackages = "sk.cyrilgavala.wardrobeapi.repository")
@EnableTransactionManagement
@ComponentScan({
	"sk.cyrilgavala.wardrobeapi.config", "sk.cyrilgavala.wardrobeapi.mapper", "sk.cyrilgavala.wardrobeapi.security", "sk.cyrilgavala.wardrobeapi.service", "sk.cyrilgavala.wardrobeapi.web"
})
public class AppConfiguration {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
