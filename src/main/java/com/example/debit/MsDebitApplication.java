package com.example.debit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MsDebitApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDebitApplication.class, args);
	}

}
