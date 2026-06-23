package com.paypal.transaction_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.transaction_manager.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${paypal.transactions.topic}")
    private String topic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransaction(Transaction transaction) {
        try {
            String payload = objectMapper.writeValueAsString(transaction);
            kafkaTemplate.send(topic, payload);
            logger.info("Published transaction to topic {}: {}", topic, transaction.getId());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize transaction {}", transaction.getId(), e);
        }
    }
}
