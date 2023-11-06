package org.ead.orderplacement.controller;

import lombok.RequiredArgsConstructor;
import org.ead.orderplacement.dto.OrderRequest;
import org.ead.orderplacement.dto.OrderResponse;
import org.ead.orderplacement.dto.OrdersResponse;
import org.ead.orderplacement.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/basic")
@RequiredArgsConstructor
public class OrderControllerBasic {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody OrderRequest orderRequest){
        return orderService.placeOrder(orderRequest);
    }

    @GetMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable String orderNumber){
        return orderService.getOrder(orderNumber);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public OrdersResponse getOrders(){
        return orderService.getAllByUserEmail("haritha@hasathcharu.com");
    }

    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public String cancelOrder(@PathVariable String orderNumber){
        orderService.cancelOrder(orderNumber);
        return "Success";
    }

}
