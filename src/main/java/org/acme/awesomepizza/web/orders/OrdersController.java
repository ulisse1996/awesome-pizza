package org.acme.awesomepizza.web.orders;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.acme.awesomepizza.service.orders.OrdersService;
import org.acme.awesomepizza.service.orders.exceptions.OrderNotFoundException;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @GetMapping
    public List<OrderModel> getAllOrder() {
        return ordersService.findAllPendingOrder(); // For our initial requirements, we only need to return pending orders
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderModel> getOrderById(@PathVariable("orderId") Long orderId) {
        try {
            return ResponseEntity.ok(ordersService.findOrderById(orderId));
        } catch (OrderNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    public OrderModel createNewOrder(@Valid @RequestBody NewOrderModel newOrder) {
        return ordersService.createNewOrder(newOrder);
    }

    @PostMapping("/{orderId}")
    public OrderModel updateOrder(@PathVariable Long orderId, @Valid @RequestBody OrderModel order) {
        order.setOrderId(orderId);
        return ordersService.updateOrder(order);
    }
}
