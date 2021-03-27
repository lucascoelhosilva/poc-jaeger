package com.coelho.paymentservice.events;

import com.coelho.paymentservice.models.Payment;
import com.coelho.paymentservice.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${payment.topic}")
    String paymentTopic;

    private final PaymentService paymentService;

    public KafkaConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @SneakyThrows
    @KafkaListener(topics = "${payment.topic}")
    void listener(String data) {
        log.info(data);

        Payment payment = objectMapper.readValue(data, Payment.class);
        paymentService.create(payment);
    }
}
