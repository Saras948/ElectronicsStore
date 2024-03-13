package com.example.electronicstore.services;

import com.example.electronicstore.dtos.OrderDto;
import com.example.electronicstore.dtos.UserDto;

import java.util.List;

public interface OrderService {

    // createOrder
    public OrderDto createOrder(OrderDto orderDto,String userId);

    // get Orders of User
    public OrderDto getOrderById(String orderId);

    //get All orders of Users
    public List<OrderDto> getAllUserOrders(String userId);


    // get All Orders
    public List<OrderDto> getAllOrders();

    // remove Order
    void removeOrder(String orderId);


}
