package org.acme.awesomepizza.web.orders;

import org.acme.awesomepizza.service.orders.OrdersService;
import org.acme.awesomepizza.service.orders.exceptions.OrderNotFoundException;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @Mock private OrdersService ordersService;
    @InjectMocks private OrdersController ordersController;

    @Test
    void should_return_empty_pending_orders() {
        assertEquals(
                Collections.emptyList(),
                ordersController.getAllOrder()
        );
    }

    @Test
    void should_return_order_by_id() {
        OrderModel model = new OrderModel();
        when(ordersService.findOrderById(3L)).thenReturn(model);
        assertEquals(model, ordersController.getOrderById(3L).getBody());
    }

    @Test
    void should_return_404_for_missing_order() {
        when(ordersService.findOrderById(3L)).thenThrow(new OrderNotFoundException(3L));
        assertEquals(404, ordersController.getOrderById(3L).getStatusCode().value());
    }

    @Test
    void should_create_new_order() {
        NewOrderModel model = new NewOrderModel();
        OrderModel created = new OrderModel();
        when(ordersService.createNewOrder(model)).thenReturn(created);
        assertEquals(created, ordersController.createNewOrder(model));
    }

    @Test
    void should_update_order() {
        OrderModel model = new OrderModel();
        OrderModel updated = new OrderModel();
        when(ordersService.updateOrder(model)).thenReturn(updated);
        assertEquals(updated, ordersController.updateOrder(3L, model));
        assertEquals(3L, model.getOrderId());
    }
}