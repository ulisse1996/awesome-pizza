package org.acme.awesomepizza.web.orders.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderModel {

    @NotNull
    private Long orderId;
    boolean completed;
    @NotEmpty private List<OrderItemModel> items;
}
