package ru.unio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.unio.repository")
public class UnioApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnioApplication.class, args);
	}

}
