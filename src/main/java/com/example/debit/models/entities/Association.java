package com.example.debit.models.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "association")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Association {
    @Id
    private String id;

    @Field(name = "associations")
    private List<Acquisition> associations = new ArrayList<>();

    @Field(name = "principal")
    private Acquisition principal;
}
