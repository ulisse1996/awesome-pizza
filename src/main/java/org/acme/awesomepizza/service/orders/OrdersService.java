package org.acme.awesomepizza.service.orders;

import lombok.RequiredArgsConstructor;
import org.acme.awesomepizza.data.orders.Order;
import org.acme.awesomepizza.data.orders.OrderStatus;
import org.acme.awesomepizza.data.orders.OrdersRepository;
import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.service.orders.exceptions.OrderNotFoundException;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersService {

    private final OrdersRepository orderRepository;
    private final OrdersMapper orderMapper;
    private final OrderValidator orderValidator;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderModel createNewOrder(NewOrderModel newOrder) {
        orderValidator.validateNewOrder(newOrder);
        Order order = orderMapper.toEntity(newOrder);
        order.setStatus(OrderStatus.RECEIVED);
        order = orderRepository.save(order);
        return orderMapper.toModel(order);
    }

    public OrderModel findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toModel)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<OrderModel> findAllPendingOrder() {
        return orderRepository.findAllByStatusNotOrderByOrderId(OrderStatus.DELIVERED)
                .stream()
                .map(orderMapper::toModel)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderModel updateOrder(OrderModel orderModel) {
        User user = orderValidator.validateLockOrder(orderModel.getOrderId());
        Order order = orderRepository.findById(orderModel.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(orderModel.getOrderId()));
        if (order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new IllegalStateException("Order is already delivered");
        }
        order.setLockedUser(orderModel.isCompleted() ? null : user);
        order.setStatus(orderModel.isCompleted() ? OrderStatus.DELIVERED : OrderStatus.findNext(order.getStatus()));
        order = orderRepository.save(order);
        return orderMapper.toModel(order);
    }
}
