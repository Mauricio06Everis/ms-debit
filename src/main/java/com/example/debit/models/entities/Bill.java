package com.example.debit.models.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Bill {
    @Field(name = "accountNumber")
    private String accountNumber;
    @Field(name = "balance")
    private Double balance;
}