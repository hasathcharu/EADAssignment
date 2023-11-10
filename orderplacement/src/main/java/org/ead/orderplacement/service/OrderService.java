package org.ead.orderplacement.service;

import lombok.RequiredArgsConstructor;
import org.ead.orderplacement.dto.*;
import org.ead.orderplacement.exception.RestException;
import org.ead.orderplacement.model.Order;
import org.ead.orderplacement.model.OrderItem;
import org.ead.orderplacement.model.OrderStatus;
import org.ead.orderplacement.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;
    public OrderResponse placeOrder(OrderRequest orderRequest, String userEmail)  {

        Order order = new Order();

        //get from user management
        UserDetailsDTO userDetails = webClientBuilder.build()
                .get()
                .uri("http://usermanagement/api/user/system/"+userEmail)
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
        InventoryResponseDTO inventoryResponseDTO = webClientBuilder.build()
                .put()
                .uri("http://inventorymanagement/api/inventory/system/place-order")
                .bodyValue(orderItems.stream().map(orderItem -> InventoryConfirmDTO.builder()
                        .productId(orderItem.getProductId())
                        .quantity(orderItem.getQuantity())
                        .build()).toList())
                .retrieve()
                .bodyToMono(InventoryResponseDTO.class)
                .onErrorResume(e -> {
                    if(e.getMessage().contains("404")){
                        throw new RestException(HttpStatus.NOT_FOUND, "Product(s) not available");
                    }
                    throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to inventory management");
                })
                .block();
        if(inventoryResponseDTO == null){
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to inventory management");
        }
        if(!inventoryResponseDTO.isSuccess()){
            String outOfStock = inventoryResponseDTO.getFailedProducts().toString();
            throw new RestException(HttpStatus.NOT_FOUND, "Product(s) out of stock: "+outOfStock);
        }
        for(OrderItem orderItem: orderItems){
            for(InventoryConfirmDTO inventoryConfirmDTO: inventoryResponseDTO.getProducts()){
                if(orderItem.getProductId().equals(inventoryConfirmDTO.getProductId())){
                    orderItem.setPrice(inventoryConfirmDTO.getPrice());
                    break;
                }
            }
        }
        order.setDate(new Date());
        order.setStatus(OrderStatus.PLACED);

        String orderNumber;
        while(true){
            orderNumber = generateOrderNumber(order);
            Optional<Order> checkOrder = orderRepository.findByOrderNumber(orderNumber);
            if(checkOrder.isEmpty()){
                break;
            }
        }
        order.setOrderNumber(orderNumber);
        order.setOrderItems(orderItems);
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

    public OrderResponse getOrder(String orderNumber, String userEmail) {
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        if(order.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, "Order not found");
        }
        System.out.println(userEmail);
        if(!order.get().getUserEmail().equals(userEmail)){
            throw new RestException(HttpStatus.UNAUTHORIZED, "Unauthorized Access");
        }
        return mapToOrderResponse(order.get());
    }

    public OrderResponse cancelOrder(String orderNumber, String userEmail) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if(order == null){
            throw new RestException(HttpStatus.NOT_FOUND, "Order not found");
        }
        if(!order.getUserEmail().equals(userEmail)){
            throw new RestException(HttpStatus.UNAUTHORIZED, "Unauthorized Access");
        }
        if(order.getStatus() != OrderStatus.PLACED){
            throw new RestException(HttpStatus.FORBIDDEN, "Order is not cancellable");
        }
        order.setStatus(OrderStatus.CANCELLED);

        //send details to inventory management to add back the quantities
        String inventoryConfirmation = webClientBuilder.build()
                .put()
                .uri("http://inventorymanagement/api/inventory/system/cancel-order")
                .bodyValue(order.getOrderItems().stream().map(orderItem -> InventoryConfirmDTO.builder()
                        .productId(orderItem.getProductId())
                        .quantity(orderItem.getQuantity())
                        .build()).toList())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to inventory management");
                })
                .block();
        if(!Objects.equals(inventoryConfirmation, "Success")){
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong cancelling order");
        }
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }
    public OrderResponse updateStatusAdmin(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if(order == null){
            throw new RestException(HttpStatus.NOT_FOUND, "Order not found");
        }
        if(order.getStatus() == OrderStatus.CANCELLED){
            throw new RestException(HttpStatus.FORBIDDEN, "Order already cancelled");
        }
        if(order.getStatus() != OrderStatus.PLACED){
            throw new RestException(HttpStatus.FORBIDDEN, "Order is already processed");
        }
        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    public OrderResponse updateStatusDeliverer(String orderNumber, String status, String deliverer) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if(order == null){
            throw new RestException(HttpStatus.NOT_FOUND, "Order not found");
        }
        if(order.getStatus() == OrderStatus.CANCELLED){
            throw new RestException(HttpStatus.FORBIDDEN, "Order already cancelled");
        }
        if(order.getStatus() == OrderStatus.PLACED) {
            throw new RestException(HttpStatus.FORBIDDEN, "Order is still processing");
        }
        if(order.getDeliveryPersonEmail() != null && !order.getDeliveryPersonEmail().equals(deliverer)){
            throw new RestException(HttpStatus.UNAUTHORIZED, "Some other delivery person has been assigned");
        }
        OrderStatus newOrderStatus;
        if(order.getStatus() == OrderStatus.PROCESSED){
            if(status.equals("pickup")){
                newOrderStatus = OrderStatus.PICKEDUP;
                order.setDeliveryPersonEmail(deliverer);
                UserDetailsDTO delivererEntity = webClientBuilder.build()
                        .get()
                        .uri("http://usermanagement/api/user/system/"+deliverer)
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
                if(delivererEntity == null){
                    throw new RestException(HttpStatus.NOT_FOUND, "User not found");
                }
                order.setDeliveryPersonEmail(delivererEntity.getEmail());
                order.setDeliveryPersonTelephone(delivererEntity.getTelephone());
                order.setDeliveryPersonName(delivererEntity.getName());
            }
            else{
                throw new RestException(HttpStatus.FORBIDDEN, "Order can only be picked up");
            }
        }
        else if(order.getStatus() == OrderStatus.PICKEDUP){
            if(status.equals("dispatched")){
                newOrderStatus = OrderStatus.DISPATCHED;
            }
            else{
                throw new RestException(HttpStatus.FORBIDDEN, "Order can only be dispatched");
            }
        }
        else if(order.getStatus() == OrderStatus.DISPATCHED){
            if(status.equals("delivered")){
                newOrderStatus = OrderStatus.DELIVERED;
            }
            else{
                throw new RestException(HttpStatus.FORBIDDEN, "Order can only be delivered");
            }
        }
        else{
            throw new RestException(HttpStatus.FORBIDDEN, "Invalid Status");
        }
        order.setStatus(newOrderStatus);
        orderRepository.save(order);
        return mapToOrderResponse(order);
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
                .deliveryPersonEmail(order.getDeliveryPersonEmail())
                .deliveryPersonName(order.getDeliveryPersonName())
                .deliveryPersonTelephone(order.getDeliveryPersonTelephone())
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
                .qty(orderItem.getQuantity())
                .unitPrice(orderItem.getPrice())
                .totalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
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


    public OrdersResponse getAllByUserEmail(String email) {
        OrdersResponse ordersResponse = new OrdersResponse();
        ordersResponse.setOrders(orderRepository.findAllByUserEmail(email).stream().map(this::mapToOrderResponse).toList());
        return ordersResponse;
    }
}

