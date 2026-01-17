package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// Explicitly register Mongo repositories because the main class package (com.example.demo)
// differs from the domain package (com.example.catalog). Without this, Spring Boot's
// auto-configuration base packages do not include the catalog module and the repository
// bean fails to register.
@SpringBootApplication(scanBasePackages = "com.example.catalog")
@EnableMongoRepositories(basePackages = "com.example.catalog.repository")
@EnableCaching // เปิดใช้งานระบบ Caching
@EnableMethodSecurity(securedEnabled = true) // เปิดใช้งานการล็อก method
@EnableKafka
public class DemoApplication {

	public static void main(String[] args) {
		System.out.println("Mongo URI = " + System.getProperty("spring.data.mongodb.uri"));
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
