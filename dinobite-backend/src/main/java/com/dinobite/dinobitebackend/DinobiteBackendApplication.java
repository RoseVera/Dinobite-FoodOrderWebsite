package com.dinobite.dinobitebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableJpaAuditing
@RestController
public class DinobiteBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(DinobiteBackendApplication.class, args);
	}

}
