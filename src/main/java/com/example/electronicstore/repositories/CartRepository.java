package com.example.electronicstore.repositories;

import com.example.electronicstore.entities.Cart;
import com.example.electronicstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,String> {
    Optional<Cart> findByUser(User user);

}
