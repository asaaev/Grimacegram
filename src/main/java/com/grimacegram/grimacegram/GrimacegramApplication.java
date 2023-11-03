package com.grimacegram.grimacegram;

import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.services.UserService;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.stream.IntStream;

@SpringBootApplication
public class GrimacegramApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrimacegramApplication.class, args);
	}

	@Bean
	@Profile("dev")
	CommandLineRunner run(UserService userService){
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				IntStream.rangeClosed(1,15).mapToObj(i -> {
					User user = new User();
					user.setUsername("user" + i);
					user.setUserDisplayName("display" + i);
					user.setPassword("P4ssword");
					return user;
				})
						.forEach(userService::save);
			}
		};
	}
}
