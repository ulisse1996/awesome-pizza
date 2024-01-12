package org.acme.awesomepizza.web.orders.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class NewOrderModel {

    @NotEmpty
    private List<OrderItemModel> items;
}
