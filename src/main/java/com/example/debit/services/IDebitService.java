package com.example.debit.services;

import com.example.debit.models.entities.Debit;
import reactor.core.publisher.Mono;

public interface IDebitService extends IBaseService<Debit,String> {
    Mono<Debit> findDebitByCardNumber(String cardNumber);
}
