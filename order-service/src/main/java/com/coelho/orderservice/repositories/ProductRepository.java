package com.coelho.orderservice.repositories;

import com.coelho.orderservice.models.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Repository
public class ProductRepository {

    private final HashMap<UUID, Product> data = new HashMap<>();

    public Product save(Product request) {
        data.put(request.getId(), request);
        return request;
    }

    public Collection<Product> findAll() {
        return data.values();
    }

    public Product findById(UUID id) {
        return data.get(id);
    }

    public void deleteById(UUID id) {
        data.remove(id);
    }

    public void deleteAll() {
        data.clear();
    }
}