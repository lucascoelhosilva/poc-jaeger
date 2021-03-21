package com.coelho.orderservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {

    private UUID productId;
    private BigInteger quantity;
    private BigDecimal price;
    private BigDecimal total;

    public BigDecimal value() {
        return BigDecimal.valueOf(this.quantity.intValue()).multiply(this.price);
    }
}