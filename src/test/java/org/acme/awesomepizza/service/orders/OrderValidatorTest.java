package org.acme.awesomepizza.service.orders;

import jakarta.validation.ValidationException;
import org.acme.awesomepizza.data.items.ItemsRepository;
import org.acme.awesomepizza.data.orders.OrderStatus;
import org.acme.awesomepizza.data.orders.OrdersRepository;
import org.acme.awesomepizza.data.users.User;
import org.acme.awesomepizza.service.users.UsersService;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderItemModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderValidatorTest {

    @Mock private OrdersRepository ordersRepository;
    @Mock private ItemsRepository itemsRepository;
    @Mock private UsersService usersService;
    @InjectMocks private OrderValidator orderValidator;

    @Test
    void should_not_throw_for_validate_new_order() {
        NewOrderModel newOrder = new NewOrderModel();
        newOrder.setItems(List.of(new OrderItemModel(1L)));
        when(itemsRepository.findAllItemsIds(List.of(1L))).thenReturn(List.of(1L));

        assertDoesNotThrow(() -> orderValidator.validateNewOrder(newOrder));
    }

    @Test
    void should_throw_exception_for_validate_new_order_and_missing_items() {
        NewOrderModel newOrder = new NewOrderModel();
        newOrder.setItems(List.of(new OrderItemModel(1L)));
        when(itemsRepository.findAllItemsIds(List.of(1L))).thenReturn(List.of());

        assertThrows(ValidationException.class, () -> orderValidator.validateNewOrder(newOrder));
    }

    @Test
    void should_throw_exception_for_duplicate_lock() {
        User user = new User();
        user.setUserId(1L);

        when(usersService.getCurrentUser()).thenReturn(user);
        when(ordersRepository.countByLockedIdEqualsAndStatusNotAndOrderIdNot(1L, OrderStatus.DELIVERED, 1L)).thenReturn(1L);

        assertThrows(ValidationException.class, () -> orderValidator.validateLockOrder(1L));
    }

    @Test
    void should_return_user_for_missing_lock() {
        User user = new User();
        user.setUserId(1L);

        when(usersService.getCurrentUser()).thenReturn(user);
        when(ordersRepository.countByLockedIdEqualsAndStatusNotAndOrderIdNot(1L, OrderStatus.DELIVERED, 1L)).thenReturn(0L);

        assertDoesNotThrow(() -> orderValidator.validateLockOrder(1L));
    }
}