package org.acme.awesomepizza.service.orders;

import org.acme.awesomepizza.data.orders.Order;
import org.acme.awesomepizza.data.orders.OrderStatus;
import org.acme.awesomepizza.data.orders.OrdersRepository;
import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.service.orders.exceptions.OrderNotFoundException;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderItemModel;
import org.acme.awesomepizza.web.orders.models.OrderModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Spy private OrdersMapper ordersMapper = new OrdersMapperImpl();
    @Mock private OrdersRepository ordersRepository;
    @Mock private OrderValidator orderValidator;
    @InjectMocks private OrdersService ordersService;

    @Test
    void should_create_new_order() {
        NewOrderModel newOrder = new NewOrderModel();
        newOrder.setItems(List.of(new OrderItemModel(1L)));

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        when(ordersRepository.save(orderArgumentCaptor.capture()))
                .then(invocationOnMock -> {
                    Order value = orderArgumentCaptor.getValue();
                    value.setOrderId(1L);
                    return value;
                });

        OrderModel orderModel = ordersService.createNewOrder(newOrder);

        assertEquals(1L, orderModel.getOrderId());
        assertEquals(1L, orderModel.getItems().get(0).getItemId());
        assertEquals(OrderStatus.RECEIVED, orderArgumentCaptor.getValue().getStatus());
        verify(orderValidator).validateNewOrder(newOrder);
    }

    @Test
    void should_return_searched_order() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.RECEIVED);
        when(ordersRepository.findById(1L)).thenReturn(java.util.Optional.of(order));

        OrderModel orderModel = ordersService.findOrderById(1L);

        assertEquals(1L, orderModel.getOrderId());
        assertFalse(orderModel.isCompleted());
    }

    @Test
    void should_not_find_searched_order() {
        when(ordersRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> ordersService.findOrderById(1L));
    }

    @Test
    void should_return_all_pending_orders() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.RECEIVED);
        Order order2 = new Order();
        order2.setOrderId(2L);
        order2.setStatus(OrderStatus.IN_PROGRESS);
        when(ordersRepository.findAllByStatusNotOrderByOrderId(OrderStatus.DELIVERED)).thenReturn(List.of(order, order2));

        List<OrderModel> orders = ordersService.findAllPendingOrder();

        assertEquals(2, orders.size());
        assertEquals(1L, orders.get(0).getOrderId());
        assertEquals(2L, orders.get(1).getOrderId());
    }

    @Test
    void should_throw_exception_when_order_is_already_delivered() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.DELIVERED);
        when(ordersRepository.findById(1L)).thenReturn(java.util.Optional.of(order));

        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(1L);
        try {
            ordersService.updateOrder(orderModel);
        } catch (Exception ex) {
            assertEquals(IllegalStateException.class, ex.getClass());
            assertEquals("Order is already delivered", ex.getMessage());
        }
    }

    @Test
    void should_return_updated_order_from_completed_request() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setLockedUser(new User());

        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(1L);
        orderModel.setCompleted(true);

        when(ordersRepository.findById(1L))
                .thenReturn(java.util.Optional.of(order));
        when(ordersRepository.save(captor.capture())).then(invocationOnMock -> captor.getValue());

        ordersService.updateOrder(orderModel);

        Order value = captor.getValue();
        assertEquals(1L, value.getOrderId());
        assertEquals(OrderStatus.DELIVERED, value.getStatus());
        assertNull(value.getLockedUser());
    }
}