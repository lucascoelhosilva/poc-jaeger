package com.coelho.orderservice.repositories;

import com.coelho.orderservice.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Repository
public class OrderRepository {

    private final HashMap<UUID, Order> data = new HashMap<>();

    public Order save(Order request) {
        data.put(request.getId(), request);
        return request;
    }

    public Collection<Order> findAll() {
        return data.values();
    }

    public Order findById(UUID id) {
        return data.get(id);
    }

    public void deleteById(UUID id) {
        data.remove(id);
    }

    public void deleteAll() {
        data.clear();
    }
}