package org.acme.awesomepizza.service.orders;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.acme.awesomepizza.data.items.ItemsRepository;
import org.acme.awesomepizza.data.orders.OrderStatus;
import org.acme.awesomepizza.data.orders.OrdersRepository;
import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.service.users.UsersService;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderItemModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderValidator {

    private final OrdersRepository ordersRepository;
    private final ItemsRepository itemsRepository;
    private final UsersService usersService;

    public void validateNewOrder(NewOrderModel newOrder) {
        List<Long> currentIds = newOrder.getItems().stream().map(OrderItemModel::getItemId).toList();
        List<Long> itemsIds = itemsRepository.findAllItemsIds(currentIds);
        if (currentIds.size() != itemsIds.size()) {
            throw new ValidationException("Found invalid items in order");
        }
    }

    public User validateLockOrder(long orderId) {
        User user = usersService.getCurrentUser();
        long count = ordersRepository.countByLockedIdEqualsAndStatusNotAndOrderIdNot(user.getUserId(), OrderStatus.DELIVERED, orderId);
        if (count > 0) {
            throw new ValidationException("User has already locked an order");
        }
        return user;
    }
}
