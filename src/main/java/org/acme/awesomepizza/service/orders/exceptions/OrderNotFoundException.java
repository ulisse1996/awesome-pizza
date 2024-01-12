package org.acme.awesomepizza.service.orders.exceptions;

import jakarta.validation.ValidationException;

public class OrderNotFoundException extends ValidationException {

    public OrderNotFoundException(Long orderId) {
        super("Order with id " + orderId + " not found");
    }
}
