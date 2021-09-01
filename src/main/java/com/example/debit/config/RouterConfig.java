package com.example.debit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.example.debit.handler.DebitHandler;

@Configuration
public class RouterConfig {
	@Bean
	public RouterFunction<ServerResponse> rutas(DebitHandler handler) {
		return route(GET("/debit"), handler::findAll)
				.andRoute(GET("/debit/{id}"), handler::findById)
				.andRoute(GET("/debit/card/{cardNumber}"), handler::findByCardNumber)
				.andRoute(GET("/debit/account/{accountNumber}"), handler::findByAccountNumber)
				.andRoute(GET("/debit/identity/{identityNumber}"), handler::findByIdentityNumber)
				.andRoute(GET("/debit/association/{cardNumber}/{iban}"), handler::associationAcquisitions)
				.andRoute(GET("/debit/disassociation/{cardNumber}/{iban}"), handler::disassociationAcquisitions)
				.andRoute(GET("/debit/main/{cardNumber}/{iban}"), handler::defineAccountAsMain)
				.andRoute(POST("/debit"), handler::save)
				.andRoute(POST("/debit/update"), handler::update);

	}
}
