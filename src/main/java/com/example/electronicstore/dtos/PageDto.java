package com.example.electronicstore.dtos;


import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDto {
    private int pageNumber;
    private int pageSize;
    private String sortBy;
    private String sortDir;

    public PageDto() {
        this.pageNumber = 0;
        this.pageSize = 10;
        this.sortBy = "createdDate";
        this.sortDir = "asc";
    }


}
