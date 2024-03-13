package com.example.electronicstore.entities;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    @Column(name = "CREATED_DT")
    private Date createdDate ;
    @Column(name = "UPDATED_DT")
    private Date updatedDate;

    @Column(name = "STATUS")
    private String status;
}
