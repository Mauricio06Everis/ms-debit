package com.example.debit.models.dto;

import com.example.debit.models.entities.Acquisition;
import com.example.debit.models.entities.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebitCreateDTO {
    private List<Acquisition> associations;
    private String accountNumber;
    private String cardNumber;
    private List<Customer> customerHolder;
    private String productName;
}
