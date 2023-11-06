package org.ead.orderplacement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.orderplacement.dto.OrderRequest;
import org.ead.orderplacement.dto.OrderResponse;
import org.ead.orderplacement.dto.OrdersResponse;
import org.ead.orderplacement.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/basic")
@RequiredArgsConstructor
public class OrderControllerBasic {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody OrderRequest orderRequest, @RequestHeader("X-User-Email") String userEmail){
        return orderService.placeOrder(orderRequest, userEmail);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public OrdersResponse getOrders(@RequestHeader("X-User-Email") String userEmail){
        return orderService.getAllByUserEmail(userEmail);
    }
    @GetMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable String orderNumber,@RequestHeader("X-User-Email") String userEmail){
        return orderService.getOrder(orderNumber, userEmail);
    }


    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public String cancelOrder(@PathVariable String orderNumber, @RequestHeader("X-User-Email") String userEmail){
        orderService.cancelOrder(orderNumber, userEmail);
        return "Success";
    }

}
