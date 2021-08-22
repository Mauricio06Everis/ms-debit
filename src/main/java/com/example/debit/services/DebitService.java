package com.example.debit.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.debit.models.entities.Debit;
import com.example.debit.repositories.IDebitRepository;
import com.example.debit.repositories.IRepository;
import reactor.core.publisher.Mono;

@Service
public class DebitService extends BaseService<Debit,String> implements IDebitService  {

	private final IDebitRepository iDebitRepository;
	
	@Autowired
	public DebitService(IDebitRepository iDebitRepository) {
		this.iDebitRepository = iDebitRepository;
	}
	
	@Override
	protected IRepository<Debit, String> getRepository() {
		return iDebitRepository;
	}

	@Override
	public Mono<Debit> findByCardNumber(String cardNumber) {
		return iDebitRepository.findByCardNumber(cardNumber);
	}
}
