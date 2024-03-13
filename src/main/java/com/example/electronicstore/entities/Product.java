package com.example.electronicstore.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "products")
public class Product extends BaseEntity{

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String title;
    @Column(name = "product_description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status")
    private ProductLiveStatus liveStatus;

    @Column(name = "product_price")
    private double productPrice;

    @Column(name = "product_image_name")
    private String productImageName;

    @Column(name = "product_quantity")
    private int quantity;
    @Column(name="stock_status")
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
