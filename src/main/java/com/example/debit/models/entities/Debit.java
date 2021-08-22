package com.example.debit.models.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "debit")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Debit {
	
	@Id
	private String id;
	
	@Field(name = "associations")
	private List<Acquisition> associations = new ArrayList<>();

	@Field(name = "principal")
	private Acquisition principal;

	@Field(name = "cardNumber")
	private String cardNumber;
	
	@Field(name = "debitDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime debitDate = LocalDateTime.now();

}
