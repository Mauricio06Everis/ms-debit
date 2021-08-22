package com.example.debit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@SpringBootApplication
public class MsDebitApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDebitApplication.class, args);
	}

	/*@Bean
	ObjectMapper objectMapper() {

		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.registerModule(new JavaTimeModule());

		return objectMapper;

	}*/
}
