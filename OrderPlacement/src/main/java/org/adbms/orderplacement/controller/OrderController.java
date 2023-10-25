package org.adbms.orderplacement.controller;

import lombok.RequiredArgsConstructor;
import org.adbms.orderplacement.dto.OrderItemResponseDTO;
import org.adbms.orderplacement.dto.OrderRequest;
import org.adbms.orderplacement.dto.OrderResponse;
import org.adbms.orderplacement.dto.OrdersResponse;
import org.adbms.orderplacement.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

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
        return orderService.getAllOrders();
    }

    @DeleteMapping("/{orderNumber}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String cancelOrder(@PathVariable String orderNumber){
        orderService.cancelOrder(orderNumber);
        return "Success";
    }

    @PutMapping("/{orderNumber}/{status}")
    @ResponseStatus(HttpStatus.OK)
    public String updateStatus(@PathVariable String orderNumber, @PathVariable String status){
        orderService.updateStatus(orderNumber, status);
        return "Success";
    }
}
