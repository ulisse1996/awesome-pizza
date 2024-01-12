package org.acme.awesomepizza.data.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByStatusNotOrderByOrderId(OrderStatus status);
    long countByLockedIdEqualsAndStatusNotAndOrderIdNot(long userId, OrderStatus status, long orderId);
}
