package com.example.debit.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.debit.models.entities.Acquisition;
import com.example.debit.services.AcquisitionService;
import com.example.debit.util.CreditCardNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.debit.models.entities.Debit;
import com.example.debit.services.IDebitService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DebitHandler {

	private final IDebitService debitService;
	private final AcquisitionService acquisitionService;
	@Autowired
	public DebitHandler(IDebitService debitService, AcquisitionService acquisitionService) {
		this.debitService = debitService;
		this.acquisitionService = acquisitionService;
	}
	
	public Mono<ServerResponse> findAll(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
					.body(debitService.findAll(), Debit.class);
	}
	
	public Mono<ServerResponse> findById(ServerRequest request) {
		String id = request.pathVariable("productId");
		return debitService.findById(id).flatMap(p -> ServerResponse.ok()
								.contentType(MediaType.APPLICATION_JSON)
								.bodyValue(p))
						.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> save(ServerRequest request) {
		log.info("DEBIT: " + request.toString());
		Mono<Debit> product = request.bodyToMono(Debit.class);
		return product.flatMap(debitService::create)
					.flatMap(p -> ServerResponse.created(URI.create("/debit/".concat(p.getId())))
							.contentType(MediaType.APPLICATION_JSON)
							.bodyValue(p))
					.onErrorResume(error -> {
						log.error("Error: "+ error);
						return Mono.error(error);
					});
						
						
	}

	public Mono<ServerResponse> associationAcquisitions(ServerRequest request){
		String cardNumber = request.pathVariable("cardNumber");
		String iban = request.pathVariable("iban");
		Mono<Debit> debit = debitService.findDebitByCardNumber(cardNumber);
		Mono<Acquisition> acquisition = acquisitionService.findByIban(iban);
		Mono<List<Acquisition>> debitList = debit.map(Debit::getAssociations);
		Mono<Debit> debitMono = Mono.just(new Debit());
		return Flux.zip(debitList, acquisition, debitMono).flatMapSequential(r -> {
			long exist = r.getT1().stream().filter(acquisition1 -> Objects.equals(acquisition1.getIban(), iban)).count();
			if (exist > 0){
				return ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue("the account you want to associate already exist");
			}
			r.getT1().add(r.getT2());
			r.getT3().setAssociations(r.getT1());
			if (r.getT3().getPrincipal() == null) {
				r.getT3().setPrincipal(r.getT2());
			}
			return debitService.update(r.getT3());
		}).collectList().flatMap(p -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(p));
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
