package com.example.debit.handler;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.debit.models.entities.Debit;
import com.example.debit.services.IDebitService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DebitHandler {

	@Autowired
	private final IDebitService debitService;
	
	@Autowired
	public DebitHandler(IDebitService debitService) {
		this.debitService = debitService;
	}
	
	public Mono<ServerResponse> findAll(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
					.body(debitService.findAll(), Debit.class);
	}
	
	public Mono<ServerResponse> findById(ServerRequest request) {
		String productoId = request.pathVariable("productoId");
		return debitService.findById(productoId).flatMap(p -> ServerResponse.ok()
								.contentType(MediaType.APPLICATION_JSON)
								.bodyValue(p))
						.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> save(ServerRequest request) {
		log.info("DEBIT: " + request.toString());
		Mono<Debit> product = request.bodyToMono(Debit.class);
		return product.flatMap(p -> {
				
						log.info("P0: " + p);
						return debitService.create(p);
					})
					.flatMap(p -> {
						log.info("P: " + p);
						/*return ServerResponse.created(URI.create("/debit/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(p);*/
						return ServerResponse.created(URI.create("/debit/".concat(p.getId())))
								.contentType(MediaType.APPLICATION_JSON)
								.bodyValue(p);
					})
					.onErrorResume(error -> {
						log.error("Error: "+ error);
						/*WebClientResponseException errorResponse = (WebClientResponseException) error;
						if(errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
							return ServerResponse.badRequest()
									.contentType(MediaType.APPLICATION_JSON)
									.bodyValue(errorResponse.getResponseBodyAsString());
						}*/
						return Mono.error(error);
					});
						
						
	}
	
	public Mono<ServerResponse> update(ServerRequest request) {
		Mono<Debit> product = request.bodyToMono(Debit.class);
		String id = request.pathVariable("id");
		return product
					.flatMap(p -> {
						p.setId(id);
						return debitService.update(p);
					})
					.flatMap(p -> ServerResponse.created(URI.create("api/product".concat(p.getId())))
							.contentType(MediaType.APPLICATION_JSON)
							.bodyValue(p)
		);
							
					
	}
	
	public Mono<ServerResponse> delete(ServerRequest request) {
		String id = request.pathVariable("id");
		return debitService.delete(id).then(ServerResponse.noContent().build());
	}
	
	
}
