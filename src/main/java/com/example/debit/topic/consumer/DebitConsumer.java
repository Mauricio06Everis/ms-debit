package com.example.debit.topic.consumer;

import com.example.debit.handler.DebitHandler;
import com.example.debit.models.dto.payload.RequestAssociateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DebitConsumer {
    private static final String SERVICE_ASSOCIATE_DEBIT_TOPIC = "service-associate-debit-topic";
    private static final String GROUP_ID = "debit-group";
    private final DebitHandler debitHandler;
    private final ObjectMapper objectMapper;

    @Autowired
    public DebitConsumer(DebitHandler debitHandler, ObjectMapper objectMapper) {
        this.debitHandler = debitHandler;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = SERVICE_ASSOCIATE_DEBIT_TOPIC, groupId = GROUP_ID)
    public Disposable retrieveAssociationAcquisitionsWithDebit(String data) throws Exception {
        log.info("data from kafka listener (acquisition) =>"+data);
        RequestAssociateDTO requestAssociateDTO= objectMapper.readValue(data, RequestAssociateDTO.class );
        return Mono.just(requestAssociateDTO)
                .as(debitHandler::associationAcquisitionsWithDebit)
                .log()
                .subscribe();
    }
}
