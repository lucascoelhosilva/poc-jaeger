package com.coelho.paymentservice.events;

import com.coelho.paymentservice.services.PaymentService;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class KafkaConsumer {

    @Value("${payment.topic}")
    String paymentTopic;

    private final TracingKafkaConsumer<String, String> kafkaPaymentConsumer;
    private final PaymentService paymentService;

    public KafkaConsumer(TracingKafkaConsumer<String, String> kafkaPaymentConsumer, PaymentService paymentService) {
        this.kafkaPaymentConsumer = kafkaPaymentConsumer;
        this.paymentService = paymentService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        listen();
    }

    private void listen() {
        ConsumerRecords<String, String> records = kafkaPaymentConsumer.poll(Duration.ofSeconds(10));

        for (ConsumerRecord<String, String> record : records) {
            String sentence = record.value();
            log.info("{}: value={}", paymentTopic, sentence);

//            paymentService.setPaymentStatus(sentence);
        }
    }
}
