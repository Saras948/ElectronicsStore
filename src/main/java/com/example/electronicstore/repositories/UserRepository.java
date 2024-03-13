package com.example.electronicstore.repositories;

import com.example.electronicstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByEmail(String email);
    List<User> findByNameContaining(String keyword);
}
