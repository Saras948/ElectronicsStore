package com.example.electronicstore.repositories;

import com.example.electronicstore.entities.Category;
import com.example.electronicstore.entities.Product;
import com.example.electronicstore.entities.ProductLiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>{

    List<Product> findByTitleContaining(String title);

    Page<Product> findByLiveStatus(ProductLiveStatus liveStatus, PageRequest pageRequest);

    Page<Product> findByCategory(Category category, PageRequest of);
}
