package com.example.electronicstore.services.impl;


import com.example.electronicstore.dtos.*;
import com.example.electronicstore.entities.*;
import com.example.electronicstore.repositories.OrderRepository;
import com.example.electronicstore.services.CartService;
import com.example.electronicstore.services.OrderService;
import com.example.electronicstore.services.UserService;
import com.example.electronicstore.utilities.Helper;
import com.example.electronicstore.utilities.ProductConvertor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private  ModelMapper modelMapper;


    @Override
    public OrderDto createOrder(OrderDto orderDto, String userId) {
        UserDto userDto = userService.getUserById(userId);
//        User user = modelMapper.map(userDto, User.class);

        if(userDto == null)
            throw new RuntimeException("User not found");
        orderDto.setUser(userDto);
        String orderId = UUID.randomUUID().toString();
        orderDto.setOrderId(orderId);
//        Order newOrder = modelMapper.map(orderDto, Order.class);
        Order newOrder = convertorderDtoToOrder(orderDto);
//        newOrder.setUser(user);
        newOrder.setCreatedDate(new Date());

        CartDto cartDto =  cartService.getCartByUser(userDto.getUserId());
//        Cart cart = modelMapper.map(cartDto, Cart.class);

        if(cartDto == null) {
            throw new RuntimeException("Cart not found");
        }


        List<CartItemDto> cartDtoItems = cartDto.getCartItems();

        if(cartDtoItems.size() < 1)
            throw new RuntimeException("Cart is empty");

        List<CartItem> cartItems = cartDtoItems.stream().map(cartItemDto -> {
            CartItem cartItem = CartItem.builder()
//                    .cart(CartServiceImpl.cartDtoToCart(cartDto))
                    .product(ProductConvertor.productDtoToProduct(cartItemDto.getProduct()))
                    .quantity(cartItemDto.getQuantity())
                    .totalPrice(cartItemDto.getTotalPrice())
                    .build();

                return  cartItem;
                }).collect(Collectors.toList());

        List<OrderItem> orderItems =  cartItems.stream().map(cartItem ->{
            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
            orderItem.setCreatedDate(new Date());
//            orderItem.setStatus("");

            return orderItem;
        }).collect(Collectors.toList());

        newOrder.setOrderAmount(cartDto.getTotalPrice());
        newOrder.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(newOrder);

        return convertOrderToOrderDto(savedOrder);
    }



    @Override
    public OrderDto getOrderById(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Order not found with id: "+orderId));
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> getAllUserOrders(String userId) {
        UserDto  userDto = userService.getUserById(userId);
        User user = modelMapper.map(userDto, User.class);
        List<Order> orders = orderRepository.findByUser(user);
        if(orders.size() < 1)
            throw new RuntimeException("No orders found for user: "+user.getName()+"");
        return orders.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if(orders.size() < 1)
            throw new RuntimeException("No orders found");
        return orders.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Order not found with id: "+orderId));
        order.setStatus("DELETE");
        orderRepository.save(order);
    }
    
    private Order convertorderDtoToOrder(OrderDto orderDto){
        Order order = Order.builder()
                .orderId(orderDto.getOrderId())
                .billingAddress(orderDto.getBillingAddress())
                .billingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .orderStatus(orderDto.getOrderStatus())
                .paymentStatus(orderDto.getPaymentStatus())
                .orderedDate(orderDto.getOrderedDate())
                .deliveredDate(orderDto.getDeliveredDate())
                .orderAmount(orderDto.getOrderAmount())
                .user(modelMapper.map(orderDto.getUser(), User.class))
                .orderItems(orderDto.getOrderItemsDto() == null ? null
                        : orderDto.getOrderItemsDto().stream().map(orderItemDto -> {
                    OrderItem orderItem = OrderItem.builder()
                            .orderItemId(orderItemDto.getOrderItemId())
                            .quantity(orderItemDto.getQuantity())
                            .totalPrice(orderItemDto.getTotalPrice())
                            .product(ProductConvertor.productDtoToProduct(orderItemDto.getProductDto()))
//                            .order(orderDto == null ? null : convertorderDtoToOrder(orderDto))
                            .build();
                    return orderItem;
                }).collect(Collectors.toList()))
                .build();
        return order;
    }

    private OrderDto convertOrderToOrderDto(Order savedOrder) {
        OrderDto orderDto = OrderDto.builder()
                .orderId(savedOrder.getOrderId())
                .billingAddress(savedOrder.getBillingAddress())
                .billingName(savedOrder.getBillingName())
                .billingPhone(savedOrder.getBillingPhone())
                .orderStatus(savedOrder.getOrderStatus())
                .paymentStatus(savedOrder.getPaymentStatus())
                .orderedDate(savedOrder.getOrderedDate())
                .deliveredDate(savedOrder.getDeliveredDate())
                .orderAmount(savedOrder.getOrderAmount())
                .user(modelMapper.map(savedOrder.getUser(), UserDto.class))
                .orderItemsDto(savedOrder.getOrderItems() == null ? null
                        : savedOrder.getOrderItems().stream().map(orderItem -> {
                    OrderItemDto orderItemDto = OrderItemDto.builder()
                            .orderItemId(orderItem.getOrderItemId())
                            .quantity(orderItem.getQuantity())
                            .totalPrice((int)orderItem.getTotalPrice())
                            .productDto(ProductConvertor.productToProductDto(orderItem.getProduct()))
//                            .orderDto(savedOrder == null ? null : convertOrderToOrderDto(savedOrder))
                            .build();
                    return orderItemDto;
                }).collect(Collectors.toList()))
                .build();
        return orderDto;
    }
}
