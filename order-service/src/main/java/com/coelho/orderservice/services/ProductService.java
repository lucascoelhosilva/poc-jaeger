package com.coelho.orderservice.services;

import com.coelho.orderservice.models.Product;
import com.coelho.orderservice.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository repository;

    public Product create(Product product) {
        log.info("Creating {}", product);
        return repository.save(product);
    }

    public Collection<Product> get() {
        log.info("Getting Products");
        return repository.findAll();
    }

    public Product get(UUID id) {
        log.info("Getting Product=[{}]", id);
        return repository.findById(id);
    }

    public void deleteById(UUID id) {
        log.info("Deleting Product=[{}]", id);
        repository.deleteById(id);
    }

    public void delete() {
        log.info("Deleting Product");
        repository.deleteAll();
    }
}