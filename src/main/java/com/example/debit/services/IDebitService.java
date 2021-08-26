package com.example.debit.services;

import com.example.debit.models.entities.Acquisition;
import com.example.debit.models.entities.Debit;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IDebitService extends IBaseService<Debit,String> {
    Mono<Debit> findByCardNumber(String cardNumber);
    Mono<Debit> findByAssociations(List<Acquisition> acquisitionList);
}
