package org.acme.awesomepizza;

import org.acme.awesomepizza.service.users.UsersService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AwesomePizzaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwesomePizzaApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner(UsersService usersService) {
		return args -> usersService.createUser("admin", "admin");
	}
}
