package org.acme.awesomepizza.service.orders;

import org.acme.awesomepizza.data.orders.Order;
import org.acme.awesomepizza.data.orders.OrderItem;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderItemModel;
import org.acme.awesomepizza.web.orders.models.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrdersMapper {

    @Mapping(target = "lockedUser", ignore = true)
    @Mapping(target = "lockedId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    Order toEntity(NewOrderModel model);

    @Mapping(target = "orderItemId", source = "itemId")
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemModel model);

    @Mapping(target = "completed", expression = "java(order.getStatus() == org.acme.awesomepizza.data.orders.OrderStatus.DELIVERED)")
    OrderModel toModel(Order order);
    OrderItemModel toModel(OrderItem orderItem);
}
