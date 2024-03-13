package com.example.electronicstore.repositories;

import com.example.electronicstore.entities.Order;
import com.example.electronicstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {
    List<Order> findByUser(User user);
}
