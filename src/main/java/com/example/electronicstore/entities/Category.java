package com.example.electronicstore.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseEntity{
    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_title" , nullable = false)
    private String title;

    @Column(name = "category_description" , length = 1000)
    private String description;

    @Column(name = "category_cover_image_name")
    private String coverImageName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}
