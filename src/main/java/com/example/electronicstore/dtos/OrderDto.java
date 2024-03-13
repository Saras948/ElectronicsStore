package com.example.electronicstore.dtos;

import com.example.electronicstore.entities.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private String orderId;
    @NotNull(message = "Billing Address is required")
    private String billingAddress;
    @NotNull(message = "Billing Name is required")
    private String billingName;
    @NotNull(message = "Billing Phone is required")
    private String billingPhone;
    private OrderStatus orderStatus = OrderStatus.PENDING;
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;
    private double orderAmount;
    private Date orderedDate = new Date();
    private Date deliveredDate;
    private UserDto user;
    private List<OrderItemDto> orderItemsDto = new ArrayList<>();
}
