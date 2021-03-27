package com.coelho.orderservice.services;

import com.coelho.orderservice.exception.NotFoundException;
import com.coelho.orderservice.models.Order;
import com.coelho.orderservice.models.Payment;
import com.coelho.orderservice.models.Status;
import com.coelho.orderservice.repositories.OrderRepository;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository repository;
    private final PaymentService paymentService;
    private final Tracer tracer;

    public void create(String userId, Order order) {
        log.info("Creating {}", order);
        repository.save(order);

        Payment payment = Payment.builder()
                                 .orderId(order.getId())
                                 .status(Status.PROCESSING)
                                 .build();

//        Span sprintSpan = tracer.buildSpan("create")
//                                .withTag("orderId", order.getId().toString())
//                                .start();

        paymentService.sendPayment(userId, payment);

//        sprintSpan.finish();
    }

    public void createAsync(Order order) {
        log.info("Creating async {}", order);
        repository.save(order);

        Payment payment = Payment.builder()
                                 .orderId(order.getId())
                                 .status(Status.PROCESSING)
                                 .build();

        Span sprintSpan = tracer.buildSpan("payment")
                                .withTag("orderId", order.getId().toString())
                                .withTag("kind", "async")
                                .start();

        paymentService.sendPaymentKafka(payment);

        sprintSpan.finish();
    }

    public Collection<Order> get() {
        log.info("Getting Orders");
        return repository.findAll();
    }

    public Order get(UUID id) {
        log.info("Getting Order=[{}]", id);

        Order order = repository.findById(id);
        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        Payment payment = paymentService.getPaymentByOrderId(id);
        order.setPayment(payment);

        return order;
    }

    public void deleteById(UUID id) {
        log.info("Deleting Order=[{}]", id);
        repository.deleteById(id);
    }

    public void delete() {
        log.info("Deleting Order");
        repository.deleteAll();
    }
}
