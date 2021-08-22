package com.example.debit.repositories;

import com.example.debit.models.entities.Debit;
import reactor.core.publisher.Mono;

public interface IDebitRepository extends IRepository<Debit,String> {
    Mono<Debit> findByCardNumber(String cardNumber);
}
