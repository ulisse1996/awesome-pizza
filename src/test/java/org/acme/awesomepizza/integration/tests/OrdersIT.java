package org.acme.awesomepizza.integration.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.awesomepizza.data.items.Item;
import org.acme.awesomepizza.data.items.ItemsRepository;
import org.acme.awesomepizza.data.orders.Order;
import org.acme.awesomepizza.data.orders.OrderStatus;
import org.acme.awesomepizza.data.orders.OrdersRepository;
import org.acme.awesomepizza.service.orders.OrdersService;
import org.acme.awesomepizza.web.orders.models.NewOrderModel;
import org.acme.awesomepizza.web.orders.models.OrderItemModel;
import org.acme.awesomepizza.web.orders.models.OrderModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class OrdersIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private OrdersService ordersService;
    @Autowired private OrdersRepository ordersRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ItemsRepository itemsRepository;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        createItems();
        this.authToken = getAuthToken();
    }

    @AfterEach
    void tearDown() {
        ordersRepository.deleteAll();
    }

    private void createItems() {
        Item item = new Item(1L, "Pizza Margherita");
        Item item2 = new Item(2L, "Pizza Marinara");
        itemsRepository.saveAll(List.of(item, item2));
    }

    private String getAuthToken() throws Exception {
        MvcResult result = this.mockMvc.perform(
                multipart("/v1/auth/login")
                        .param("username", "admin")
                        .param("password", "admin")
        ).andReturn();
        List<String> headers = result.getResponse().getHeaders("awesome-pizza-jwt");
        assertEquals(1, headers.size());
        return headers.get(0);
    }

    @Test
    void should_return_403_for_unauthorized_user_and_list_orders() throws Exception {
        this.mockMvc.perform(
                        get("/api/v1/orders")
                ).andDo(print())
                .andExpect(status().is(403));
    }

    @Test
    void should_return_403_for_unauthorized_user_and_update_order() throws Exception {
        OrderModel orderModel = new OrderModel();
        this.mockMvc.perform(
                        post("/api/v1/orders/1")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsBytes(orderModel))
                ).andDo(print())
                .andExpect(status().is(403));
    }

    @Test
    void should_return_all_pending_orders() throws Exception {
        NewOrderModel newOrderModel = generateOrderModel(2L);
        NewOrderModel newOrderModel2 = generateOrderModel(1L);

        List<OrderModel> models = Stream.of(newOrderModel, newOrderModel2).map(ordersService::createNewOrder).toList();
        this.mockMvc.perform(
                        get("/api/v1/orders")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                ).andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].orderId").value(models.get(0).getOrderId()))
                .andExpect(jsonPath("$[1].orderId").value(models.get(1).getOrderId()));
    }

    @Test
    void should_lock_order() throws Exception {
        NewOrderModel newOrderModel = generateOrderModel(2L);
        OrderModel orderModel = ordersService.createNewOrder(newOrderModel);
        this.mockMvc.perform(
                        post(String.format("/api/v1/orders/%d", orderModel.getOrderId()))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(orderModel))
                ).andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.orderId").value(orderModel.getOrderId()))
                .andExpect(jsonPath("$.completed").value(false));
        Optional<Order> byId = ordersRepository.findById(orderModel.getOrderId());
        assertTrue(byId.isPresent());
        assertEquals(OrderStatus.IN_PROGRESS, byId.get().getStatus());
    }

    @Test
    void should_return_request_order() throws Exception {
        NewOrderModel newOrderModel = generateOrderModel(2L);
        OrderModel orderModel = ordersService.createNewOrder(newOrderModel);
        this.mockMvc.perform(
                        get(String.format("/api/v1/orders/%d", orderModel.getOrderId()))
                ).andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.orderId").value(orderModel.getOrderId()))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void should_return_404_for_missing_order() throws Exception {
        this.mockMvc.perform(
                        get("/api/v1/orders/1")
                ).andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    void should_return_400_for_constrains_violation_on_new_order() throws Exception {
        NewOrderModel orderModel = new NewOrderModel();
        this.mockMvc.perform(
                        put("/api/v1/orders")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsBytes(orderModel))
                ).andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    void should_throw_exception_for_duplicate_order_lock() throws Exception {
        NewOrderModel newOrderModel = generateOrderModel(1L);
        NewOrderModel newOrderModel2 = generateOrderModel(2L);

        OrderModel orderModel = ordersService.createNewOrder(newOrderModel);
        OrderModel orderModel2 = ordersService.createNewOrder(newOrderModel2);

        this.mockMvc.perform(
                        post(String.format("/api/v1/orders/%d", orderModel.getOrderId()))
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsBytes(orderModel))
                ).andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.orderId").value(orderModel.getOrderId()))
                .andExpect(jsonPath("$.completed").value(false));

        this.mockMvc.perform(
                        post(String.format("/api/v1/orders/%d", orderModel2.getOrderId()))
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsBytes(orderModel2))
                ).andDo(print())
                .andExpect(status().is(400));
    }

    private NewOrderModel generateOrderModel(long id) {
        OrderItemModel orderItemModel = new OrderItemModel();
        orderItemModel.setItemId(id);
        NewOrderModel newOrderModel = new NewOrderModel();
        newOrderModel.setItems(List.of(orderItemModel));
        return newOrderModel;
    }
}
