package com.coelho.orderservice.services;

import com.coelho.orderservice.models.Payment;
import com.google.gson.JsonObject;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

    @Value("${api-payment.url}")
    private String apiPaymentUrl;

    @Value("${payment.topic}")
    String paymentTopic;

    private final RestTemplate restTemplate;
    private final TracingKafkaProducer<String, String> kafkaProducer;

    public PaymentService(RestTemplate restTemplate, TracingKafkaProducer<String, String> kafkaProducer) {
        this.restTemplate = restTemplate;
        this.kafkaProducer = kafkaProducer;
    }

    @SneakyThrows
    public void sendPayment(Payment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.put("userId", List.of("test"));

        HttpEntity<Payment> request = new HttpEntity<>(payment, headers);

        ResponseEntity<Void> response = restTemplate
                .exchange(apiPaymentUrl.concat("/payments"), HttpMethod.POST, request, Void.class);

        log.info("Response status payment {}", response.getStatusCode());
    }

    public Payment getPaymentByOrderId(UUID orderId) {
        ResponseEntity<Payment> response = restTemplate
                .getForEntity(apiPaymentUrl.concat("/payments/" + orderId), Payment.class);

        log.info("Response status payment {}", response.getStatusCode());

        return response.getBody();
    }

    public void sendPaymentKafka(Payment payment) {
        JsonObject json = new JsonObject();
        json.addProperty("orderId", payment.getOrderId().toString());
        String jsonString = json.toString();

        ProducerRecord<String, String> record = new ProducerRecord<>(paymentTopic, jsonString);
        kafkaProducer.send(record, (RecordMetadata recordMetadata, Exception exception) -> {
            if (exception != null) {
                log.error("Exception producing answer", exception);
            }
        });

        log.info("{}: value={}", paymentTopic, payment.toString());
    }

}
