package com.example.electronicstore.services;

import com.example.electronicstore.dtos.PageDto;
import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.dtos.ProductDto;

import java.util.List;

public interface ProductService {

    //create product
    public ProductDto createProduct(ProductDto productDto);

    //update product
    public ProductDto updateProduct(ProductDto productDto, String productId);

    //delete product
    public void deleteProduct(String productId);

    //get product by id
    public ProductDto getProductById(String productId);

    //get all products
    public PageableResponse<ProductDto> getAllProducts(PageDto pageDto);

    //get all products : live
    public PageableResponse<ProductDto> getAllProductsLive(String isLive,PageDto pageDto);

    //search products
    public List<ProductDto> searchProducts(String search);

    //create product with Category
    public ProductDto createProductWithCategory(ProductDto productDto, String categoryId);

    //update product with Category
    ProductDto updateProductWithCategory(String categoryId, String productId);

    PageableResponse<ProductDto> getAllProductsByCategory(String categoryId, PageDto pageDto);
}
