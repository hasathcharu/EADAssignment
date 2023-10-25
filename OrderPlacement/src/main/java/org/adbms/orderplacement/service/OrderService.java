package org.adbms.orderplacement.service;

import lombok.RequiredArgsConstructor;
import org.adbms.orderplacement.dto.*;
import org.adbms.orderplacement.exception.RestException;
import org.adbms.orderplacement.model.Order;
import org.adbms.orderplacement.model.OrderItem;
import org.adbms.orderplacement.model.OrderStatus;
import org.adbms.orderplacement.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;
    public OrderResponse placeOrder(OrderRequest orderRequest)  {

        Order order = new Order();

        //get from user management
        UserDetailsDTO userDetails = webClientBuilder.build()
                .get()
                .uri("http://UserManagement/api/user/"+orderRequest.getUserEmail())
                .retrieve()
                .bodyToMono(UserDetailsDTO.class)
                .onErrorResume(e -> {
                    if(e.getMessage().contains("404")){
                        throw new RestException(HttpStatus.NOT_FOUND, "User not found");
                    }
                    else{
                        throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to user management");
                    }
                })
                .block();
        if(userDetails == null){
            throw new RestException(HttpStatus.NOT_FOUND, "User not found");
        }
        order.setUserEmail(userDetails.getEmail());
        order.setUserName(userDetails.getName());
        order.setUserAddress(userDetails.getAddress());
        order.setUserTelephone(userDetails.getTelephone());

        //confirm quantity availability and get prices from inventory management

        List<OrderItem> orderItems = orderRequest.getOrderItems()
                .stream()
                .map(this::mapToEntity)
                .toList();

        order.setOrderItems(orderItems);
        List<InventoryConfirmDTO> inventoryConfirmDTOList = webClientBuilder.build()
                .put()
                .uri("http://InventoryManagement/api/inventory/PlaceOrder")
                .bodyValue(orderItems.stream().map(orderItem -> InventoryConfirmDTO.builder()
                        .pid(orderItem.getProductId())
                        .qty(orderItem.getQuantity())
                        .build()).toList())
                .retrieve()
                .bodyToFlux(InventoryConfirmDTO.class)
                .onErrorResume(e -> {
                    System.out.println(e.getMessage());
                    if(e.getMessage().contains("404")){
                        throw new RestException(HttpStatus.NOT_FOUND, "Product(s) out of stock");
                    }
                    else{
                        throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to inventory management");
                    }
                })
                .collectList().block();
        if(inventoryConfirmDTOList == null){
            throw new RestException(HttpStatus.NOT_FOUND, "Product(s) out of stock");
        }
        //set prices
        for(OrderItem orderItem: orderItems){
            for(InventoryConfirmDTO inventoryConfirmDTO: inventoryConfirmDTOList){
                if(orderItem.getProductId() == inventoryConfirmDTO.getPid()){
                    orderItem.setPrice(inventoryConfirmDTO.getPrice());
                }
            }
        }
        order.setDate(new Date());
        order.setStatus(OrderStatus.PLACED);
        order.setOrderNumber(generateOrderNumber(order));
        this.orderRepository.save(order);
        return mapToOrderResponse(order);
    }



    public OrdersResponse getAllOrders() {
        OrdersResponse ordersResponse = new OrdersResponse();
        ordersResponse.setOrders(orderRepository.findAll().stream().map(this::mapToOrderResponse).toList());
        return ordersResponse;
    }


    public OrderResponse getOrder(String orderNumber) {
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        if(order.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return mapToOrderResponse(order.get());
    }

    public void cancelOrder(String orderNumber) {
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        if(order.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, "Order not found");
        }
        if(order.get().getStatus() == OrderStatus.COMPLETED){
            throw new RestException(HttpStatus.FORBIDDEN, "Order completed");
        }
        order.get().setStatus(OrderStatus.CANCELLED);
        //send details to inventory management to add back the quantities
        String inventoryConfirmation = webClientBuilder.build()
                .put()
                .uri("http://InventoryManagement/api/inventory/CancelOrder")
                .bodyValue(order.get().getOrderItems().stream().map(orderItem -> InventoryConfirmDTO.builder()
                        .pid(orderItem.getProductId())
                        .qty(orderItem.getQuantity())
                        .build()).toList())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    System.out.println(e.getMessage());
                    throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to inventory management");
                })
                .block();
        if(inventoryConfirmation == null){
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to inventory management");
        }
        orderRepository.save(order.get());
    }
    public void updateStatus(String orderNumber, String status) {
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        if(order.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, "Order not found");
        }
        if(order.get().getStatus() == OrderStatus.CANCELLED){
            throw new RestException(HttpStatus.FORBIDDEN, "Order already cancelled");
        }
        //can be expanded to accommodate more statuses
        switch (status){
            case "completed":
                order.get().setStatus(OrderStatus.COMPLETED);
                break;
            default:
                throw new RestException(HttpStatus.BAD_REQUEST, "Invalid Status");
        }
        orderRepository.save(order.get());
    }
    private OrderResponse mapToOrderResponse(Order order){
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .date(order.getDate())
                .userEmail(order.getUserEmail())
                .userName(order.getUserName())
                .userAddress(order.getUserAddress())
                .userTelephone(order.getUserTelephone())
                .orderItems(order.getOrderItems().stream().map(this:: mapToOrderItemsDTO).toList())
                .status(order.getStatus())
                .total(calculateOrderTotal(order.getOrderItems()))
                .build();
    }
    private BigDecimal calculateOrderTotal(List<OrderItem> orderItems){
        BigDecimal total = new BigDecimal(0);
        for(OrderItem orderItem: orderItems){
            total = total.add(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())));
        }
        return total;
    }
    private OrderItemResponseDTO mapToOrderItemsDTO(OrderItem orderItem){
        return OrderItemResponseDTO.builder()
                .pid(orderItem.getProductId())
                .itemId(orderItem.getItemId())
                .qty(orderItem.getQuantity())
                .unitPrice(orderItem.getPrice())
                .totalPrice(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())))
                .build();
    }
    private OrderItem mapToEntity(OrderItemDTO orderItemDTO){
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(orderItemDTO.getPid());
        orderItem.setQuantity(orderItemDTO.getQty());
        return orderItem;
    }
    private String generateOrderNumber(Order order){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmm").format(order.getDate());
        return order.getUserEmail() + "_" + timeStamp + "_" + new Random().nextInt(1001);
    }


}

