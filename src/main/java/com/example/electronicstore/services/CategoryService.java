package com.example.electronicstore.services;

import com.example.electronicstore.dtos.CategoryDto;
import com.example.electronicstore.dtos.PageDto;
import com.example.electronicstore.dtos.PageableResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CategoryService {

    public CategoryDto createCategory(CategoryDto categoryDto);


    public CategoryDto updateCategory(CategoryDto categoryDto, String CategoryId);

    public CategoryDto getCategory(String categoryId);

    public PageableResponse<CategoryDto> getAllCategories(PageDto pageDto);

    public void deleteCategory(String categoryId) throws IOException;

}
