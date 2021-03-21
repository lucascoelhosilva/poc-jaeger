package com.coelho.paymentservice.services;

import com.coelho.paymentservice.models.Payment;
import com.coelho.paymentservice.models.Status;
import com.coelho.paymentservice.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository repository;

    public Payment create(Payment payment) {
        log.info("Creating {}", payment);
        payment.setStatus(Status.APPROVED);
        return repository.save(payment);
    }

    public void setPaymentStatus(UUID id, Status status) {
        log.info("setPaymentStatus id {}", id);

        Payment payment = get(id);
        payment.setStatus(status);

        repository.save(payment);
    }

    public Collection<Payment> get() {
        log.info("Getting Payments");
        return repository.findAll();
    }

    public Payment get(UUID id) {
        log.info("Getting Payment=[{}]", id);
        return repository.findByOrderId(id);
    }

    public void deleteById(UUID id) {
        log.info("Deleting Payment=[{}]", id);
        repository.deleteById(id);
    }

    public void delete() {
        log.info("Deleting Payment");
        repository.deleteAll();
    }

    @SneakyThrows
    private void processingPayment(Payment payment) {
        TimeUnit.SECONDS.sleep(15);
        payment.setStatus(Status.APPROVED);
        repository.save(payment);
    }
}