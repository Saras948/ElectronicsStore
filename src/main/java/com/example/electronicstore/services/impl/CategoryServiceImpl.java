package com.example.electronicstore.services.impl;

import com.example.electronicstore.dtos.*;
import com.example.electronicstore.entities.*;
import com.example.electronicstore.exception.ResourceNotFoundException;
import com.example.electronicstore.repositories.CategoryRepository;
import com.example.electronicstore.services.CategoryService;
import com.example.electronicstore.utilities.Helper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${category.image.path}")
    private String imageUploadPath;

    private Logger logger = Helper.getLogger(CategoryServiceImpl.class);

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        String categoryId = UUID.randomUUID().toString();
        Category category = modelMapper.map(categoryDto, Category.class);
        category.setCategoryId(categoryId);
        category.setCreatedDate(Helper.getCurrentDate());
        category.setStatus("Active");
        logger.info("Category title : {}", category.getTitle());
        Category savedCategory = categoryRepository.save(category);

        CategoryDto saveCategoryDto = modelMapper.map(savedCategory, CategoryDto.class);

        return saveCategoryDto;
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, String CategoryId) {

        Category category = categoryRepository.findById(CategoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id !!"));
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImageName(categoryDto.getCoverImageName());
        category.setUpdatedDate(Helper.getCurrentDate());

        Category updateCategory = categoryRepository.save(category);

        return modelMapper.map(updateCategory,CategoryDto.class);
    }

    @Override
    public CategoryDto getCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id !!"));
        return modelMapper.map(category,CategoryDto.class);
    }

    @Override
    public PageableResponse<CategoryDto> getAllCategories(PageDto pageDto) {
        Sort sort = (pageDto.getSortDir().equalsIgnoreCase("Desc")) ?
                (Sort.by(pageDto.getSortBy()).descending()): (Sort.by(pageDto.getSortBy()).ascending());


        Page<Category> page = categoryRepository.findAll(PageRequest.of(pageDto.getPageNumber(), pageDto.getPageSize(), sort));

        //convert user page to  userDto pageResponse
        PageableResponse<CategoryDto> response = Helper.getPageableResponse(page, CategoryDto.class);

        return response;
    }

    @Override
    public void deleteCategory(String categoryId)  {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id"));

        String coverImageName = category.getCoverImageName();
        try {
            String fullPath = imageUploadPath + File.separator + coverImageName;

            Path path = Path.of(fullPath);
            Files.delete(path);
        }
        catch (NoSuchFileException ex)
        {
            logger.error("Image not found with name: {}" ,ex.getMessage());
        }
        catch (IOException ex)
        {
            logger.error("Error occurred while deleting image: {}", ex.getMessage());
            ex.printStackTrace();
        }

        categoryRepository.delete(category);
    }
}
