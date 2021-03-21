package com.coelho.paymentservice.repositories;

import com.coelho.paymentservice.models.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Repository
public class PaymentRepository {

    private final HashMap<UUID, Payment> data = new HashMap<>();

    public Payment save(Payment request) {
        data.put(request.getOrderId(), request);
        return request;
    }

    public Collection<Payment> findAll() {
        return data.values();
    }

    public Payment findByOrderId(UUID id) {
        return data.get(id);
    }

    public void deleteById(UUID id) {
        data.remove(id);
    }

    public void deleteAll() {
        data.clear();
    }
}