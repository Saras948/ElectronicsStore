package com.example.electronicstore.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "cart")
public class Cart extends BaseEntity{

    @Id
    @Column(name = "cart_id")
    private String cartId;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();

}
