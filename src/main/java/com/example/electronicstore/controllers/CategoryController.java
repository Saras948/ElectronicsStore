package com.example.electronicstore.controllers;


import com.example.electronicstore.dtos.*;
import com.example.electronicstore.entities.Category;
import com.example.electronicstore.services.CategoryService;
import com.example.electronicstore.services.FileService;
import com.example.electronicstore.services.ProductService;
import com.example.electronicstore.utilities.Helper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.el.stream.Stream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Value("${category.image.path}")
    private String imageUploadPath;
    private Logger logger = Helper.getLogger(CategoryController.class);

    //createCategory
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto){
        logger.info("Category title : {}", categoryDto.getTitle());
        CategoryDto savedCategoryDto = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategoryDto, HttpStatus.CREATED);
    }

    //updateCategory
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String categoryId,
                                                       @Valid @RequestBody CategoryDto category){
        CategoryDto updatedCategory = categoryService.updateCategory(category,categoryId);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }

    //getCategory By categoryId
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable String categoryId){
        CategoryDto categoryDto = categoryService.getCategory(categoryId);
        return new ResponseEntity<>(categoryDto,HttpStatus.OK);
    }

    //getAllCategories
    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategories(@RequestBody PageDto pageDetail) {
        PageableResponse<CategoryDto> categoryDtoList = categoryService.getAllCategories(pageDetail);
        return new ResponseEntity<>(categoryDtoList,HttpStatus.OK);
    }

    //deleteCategory
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId) throws IOException {
        categoryService.deleteCategory(categoryId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Category deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @PostMapping("/image/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCategoryImage(@PathVariable String categoryId,
                                                             @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileService.uploadFile(file,imageUploadPath);

        CategoryDto category = categoryService.getCategory(categoryId);
        category.setCoverImageName(fileName);
        CategoryDto categoryDto = categoryService.updateCategory(category,categoryId);
        ImageResponse response = ImageResponse.builder()
                .imageName(fileName)
                .message("Image uploaded successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/image/{categoryId}")
    public void getCategoryImage(@PathVariable String categoryId,
                                 HttpServletResponse response) throws IOException {
        String imageName = categoryService.getCategory(categoryId).getCoverImageName();
        InputStream resource = fileService.getResource(imageName,imageUploadPath);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        StreamUtils.copy(resource,response.getOutputStream());
    }

    //create product with Category
    @PostMapping("/{categoryId}/products")
    public ResponseEntity<ProductDto> createProductWithCategory(@Valid @RequestBody ProductDto productDto, @PathVariable String categoryId){
        ProductDto product = productService.createProductWithCategory(productDto, categoryId);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}/products/{productId}")
    public ResponseEntity<ProductDto> updateProductWithCategory(@PathVariable String categoryId, @PathVariable String productId){
        ProductDto product = productService.updateProductWithCategory(categoryId, productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<PageableResponse> getAllProductByCategory(@PathVariable String categoryId, @RequestBody PageDto pageDto){
        PageableResponse<ProductDto> products = productService.getAllProductsByCategory(categoryId, pageDto);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
