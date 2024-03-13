package com.example.electronicstore.utilities;

import com.example.electronicstore.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Helper {
    public static <U,V> PageableResponse<V> getPageableResponse(Page<U> page,Class<V> type){
        List<U> entity =  page.getContent();
        List<V> dtoList = entity.stream().map(object -> new ModelMapper().map(object,type)).collect(Collectors.toList());
        PageableResponse<V> response = PageableResponse.<V>builder()
                .content(dtoList)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();
        return response;
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static <V> Logger getLogger(Class<V> type) {
        return LoggerFactory.getLogger(type);
    }
}
