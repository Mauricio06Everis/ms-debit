package com.example.debit.repositories;

import com.example.debit.models.entities.Acquisition;
import com.example.debit.models.entities.Debit;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IDebitRepository extends IRepository<Debit,String> {
    Mono<Debit> findByCardNumber(String cardNumber);
    Mono<Debit> findByAssociationsContains(List<Acquisition> acquisitionList);
}
