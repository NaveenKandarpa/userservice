package com.example.userservice.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaProducerConfig {
    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerConfig(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // sendMessage will push a given event into Kafka
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
