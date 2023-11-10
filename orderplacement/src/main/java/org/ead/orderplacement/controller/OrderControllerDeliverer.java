package org.ead.orderplacement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.orderplacement.dto.OrderRequest;
import org.ead.orderplacement.dto.OrderResponse;
import org.ead.orderplacement.dto.OrdersResponse;
import org.ead.orderplacement.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/deliverer")
@RequiredArgsConstructor
public class OrderControllerDeliverer {

    private final OrderService orderService;

    @PutMapping("/{orderId}/{status}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateStatus(@PathVariable String orderId, @PathVariable String status, @RequestHeader("X-User-Email") String deliverer){
        return orderService.updateStatusDeliverer(orderId, status, deliverer);
    }


    @GetMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable String orderNumber){
        return orderService.getOrder(orderNumber);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public OrdersResponse getOrders(){
        return orderService.getAllOrders();
    }
}
