package com.example.debit.handler;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.debit.models.dto.DebitCreateDTO;
import com.example.debit.models.entities.Acquisition;
import com.example.debit.services.AcquisitionService;
import com.example.debit.util.CreditCardNumberGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
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
						.switchIfEmpty(Mono.error(new RuntimeException("Debit card not found")));
	}
	public Mono<ServerResponse> findByCardNumber(ServerRequest request) {
		String cardNumber = request.pathVariable("cardNumber");
		return debitService.findByCardNumber(cardNumber).flatMap(p -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(p))
				.switchIfEmpty(Mono.error(new RuntimeException("Debit card not found")));
	}
	public Mono<ServerResponse> save(ServerRequest request) {
		Mono<Debit> debitRequest = request.bodyToMono(Debit.class);
		return debitRequest
				.zipWhen(debit -> {
					log.info("ACQUSITION, {}", acquisitionService.findByIban(debit.getPrincipal().getIban()));
					return acquisitionService.findByIban(debit.getPrincipal().getIban());
				})
				.flatMap(result -> {
					List<Acquisition> associations = result.getT1().getAssociations();
					associations.add(result.getT2());
					log.info("LIST, {}", associations);
					result.getT1().setAssociations(associations);
					result.getT1().setCardNumber(new CreditCardNumberGenerator().generate("4551", 17));
					result.getT1().setPrincipal(result.getT2());
					return debitService.create(result.getT1());
				})
				.switchIfEmpty(Mono.error(new RuntimeException("debit created failed")))
				.checkpoint("after debit created")
				.flatMap(debit -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(debit))
				.log()
				.onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
	}


	public Mono<ServerResponse> associationAcquisitions(ServerRequest request) {
		String cardNumber = request.pathVariable("cardNumber");
		String iban = request.pathVariable("iban");
		Mono<Debit> debit = debitService.findByCardNumber(cardNumber);
		Mono<Acquisition> acquisition = acquisitionService.findByIban(iban);
		return debit.zipWith(acquisition, (deb, acq) -> {
			long existAcquisition = deb.getAssociations().stream().filter(d -> Objects.equals(d.getIban(), iban)).count();
			if (existAcquisition > 0){
				return Mono.error(new RuntimeException("the account you want to associate already exist"));
			}
			List<Acquisition> associations = deb.getAssociations();
			associations.add(acq);
			deb.setAssociations(associations);
			return debitService.update(deb);
		})
				.switchIfEmpty(Mono.error(new RuntimeException("debit association failed")))
				.flatMap(Mono::checkpoint)
				.flatMap(debitResponse -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(debitResponse))
				.log()
				.onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
	}
	
	public Mono<ServerResponse> disassociationAcquisitions(ServerRequest request) {
		String cardNumber = request.pathVariable("cardNumber");
		String iban = request.pathVariable("iban");
		Mono<Debit> debit = debitService.findByCardNumber(cardNumber);
		Mono<Acquisition> acquisition = acquisitionService.findByIban(iban);
		return debit.zipWith(acquisition, (deb, acq) -> {
					long existAcquisition = deb.getAssociations().stream().filter(d -> Objects.equals(d.getIban(), iban)).count();
					Boolean isPrincipal = deb.getPrincipal().getIban().equals(acq.getIban());
					List<Acquisition> associations = deb.getAssociations();
					if (existAcquisition == 0){
						return Mono.error(new RuntimeException("the account you want to disassociate does not exist"));
					}
					associations.remove(acq);
					if (Boolean.TRUE.equals(isPrincipal)){
						Double maxBalance= associations.stream()
								.map(asc -> asc.getBill().getBalance())
								.max(Comparator.comparing(i -> i))
								.orElse(0.0);

						Acquisition acquisitionMaxBalance = associations
								.stream()
								.filter(acquisition1 -> Objects.equals(acquisition1.getBill().getBalance(), maxBalance))
								.findFirst()
								.orElse(new Acquisition());
						//deb.setPrincipal(associations.get(associations.size() - 1));
						deb.setPrincipal(acquisitionMaxBalance);
					}
					deb.setAssociations(associations);
					return debitService.update(deb);
				})
				.switchIfEmpty(Mono.error(new RuntimeException("debit set main account failed")))
				.flatMap(Mono::checkpoint)
				.flatMap(debitResponse -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(debitResponse))
				.log()
				.onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
	}

	public Mono<ServerResponse> defineAccountAsMain(ServerRequest request) {
		String iban = request.pathVariable("iban");
		String cardNumber = request.pathVariable("cardNumber");
		Mono<Debit> debit = debitService.findByCardNumber(cardNumber);
		Mono<Acquisition> acquisition = acquisitionService.findByIban(iban);
		return Mono.zip(debit, acquisition)
				.flatMap(data -> {
					long existAcquisition = data.getT1().getAssociations().stream().filter(d -> Objects.equals(d.getIban(), iban)).count();
					Boolean isPrincipal = data.getT1().getPrincipal().getIban().equals(data.getT2().getIban());
					if (existAcquisition == 0){
						return Mono.error(new RuntimeException("the account you want to disassociate does not exist"));
					}
					if (Boolean.TRUE.equals(isPrincipal)){
						return Mono.error(new RuntimeException("this account is already the main one"));
					}
					data.getT1().setPrincipal(data.getT2());
					return debitService.update(data.getT1());
				})
				.flatMap(debitResponse -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(debitResponse))
				.log()
				.onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
	}
	public Mono<ServerResponse> update(ServerRequest request) {
		Mono<Debit> debitRequest = request.bodyToMono(Debit.class);
		return debitRequest
				.zipWhen(debit -> debitService.findByCardNumber(debit.getCardNumber()))
				.flatMap(data -> {
					data.getT2().setAssociations(data.getT1().getAssociations());
					data.getT2().setPrincipal(data.getT1().getPrincipal());
					return debitService.update(data.getT2());
				})
				.flatMap(debitResponse -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(debitResponse))
				.log()
				.onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
	}
}
