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
@RequestMapping("/api/order/admin")
@RequiredArgsConstructor
public class OrderControllerAdmin {

    private final OrderService orderService;

    @GetMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrder(@PathVariable String orderNumber){
        return orderService.getOrder(orderNumber);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public OrdersResponse getOrders(){
        System.out.println("Hello");
        return orderService.getAllOrders();
    }


    @PutMapping("/status/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse updateStatus(@PathVariable String orderNumber){
        return orderService.updateStatus(orderNumber);
    }
}
