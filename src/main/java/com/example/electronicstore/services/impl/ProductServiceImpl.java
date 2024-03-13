package com.example.electronicstore.services.impl;

import com.example.electronicstore.dtos.CategoryDto;
import com.example.electronicstore.dtos.PageDto;
import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.dtos.ProductDto;
import com.example.electronicstore.entities.Category;
import com.example.electronicstore.entities.Product;
import com.example.electronicstore.entities.ProductLiveStatus;
import com.example.electronicstore.entities.StockStatus;
import com.example.electronicstore.exception.ResourceNotFoundException;
import com.example.electronicstore.repositories.ProductRepository;
import com.example.electronicstore.services.CategoryService;
import com.example.electronicstore.services.ProductService;
import com.example.electronicstore.utilities.Helper;

import com.example.electronicstore.utilities.ProductConvertor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Value("${product.image.path}")
    private String productImagePath;

    @Autowired
    private ModelMapper modelMapper;

    private Logger logger = Helper.getLogger(ProductServiceImpl.class);

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        String productId = UUID.randomUUID().toString();
        productDto.setProductId(productId);
        Product product = ProductConvertor.productDtoToProduct(productDto);
        product.setLiveStatus(ProductLiveStatus.NOT_LIVE);
        product.setCreatedDate(Helper.getCurrentDate());
        Product savedProduct = productRepository.save(product);
        return ProductConvertor.productToProductDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto, String productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setLiveStatus(productDto.getLiveStatus().equalsIgnoreCase("LIVE") ? ProductLiveStatus.LIVE : ProductLiveStatus.NOT_LIVE);
        product.setProductPrice(productDto.getProductPrice());
        product.setQuantity(Integer.parseInt(productDto.getQuantity()));
        product.setUpdatedDate(Helper.getCurrentDate());
        product.setProductImageName(productDto.getProductImageName());
        product.setStockStatus(productDto.getStockStatus().equalsIgnoreCase("IN_STOCK") ? StockStatus.IN_STOCK : StockStatus.OUT_OF_STOCK);
        Product savedProduct = productRepository.save(product);

        return ProductConvertor.productToProductDto(savedProduct);
    }

    @Override
    public void deleteProduct(String productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        String imageName = product.getProductImageName();
        String fullPath = productImagePath + imageName;

        logger.info("Deleting image: {}", fullPath);
        Path path = Path.of(fullPath);
        try {
            Files.delete(path);
        }
        catch (NoSuchFileException e){
            logger.error("File not found: {}", fullPath);
            logger.error("Error message: {}", e.getMessage());
        }
        catch (Exception e){
            logger.error("Error deleting file: {}" , fullPath);
            logger.error("Error message: {}", e.getMessage());
        }

        productRepository.delete(product);
    }

    @Override
    public ProductDto getProductById(String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return ProductConvertor.productToProductDto(product);
    }

    @Override
    public PageableResponse<ProductDto> getAllProducts(PageDto pageDto) {

        Sort sort = Sort.by(pageDto.getSortBy());
        sort = pageDto.getSortDir().equals("desc") ? sort.descending() : sort.ascending();

        Page<Product> page =
            productRepository.findAll(PageRequest.of(pageDto.getPageNumber(),pageDto.getPageSize(),sort));

        Page<ProductDto> product = page.map(product1 -> ProductConvertor.productToProductDto(product1));

        PageableResponse<ProductDto> pageableResponse = Helper.getPageableResponse(product, ProductDto.class);

        return pageableResponse;
    }

    @Override
    public PageableResponse<ProductDto> getAllProductsLive(String isLive,PageDto pageDto) {

        ProductLiveStatus productLiveStatus = isLive.equalsIgnoreCase("LIVE") ? ProductLiveStatus.LIVE : ProductLiveStatus.NOT_LIVE;

        Sort sort = Sort.by(pageDto.getSortBy());
        sort = pageDto.getSortDir().equals("desc") ? sort.descending() : sort.ascending();

        Page<Product> page =
                productRepository.findByLiveStatus(productLiveStatus,PageRequest.of(pageDto.getPageNumber(),pageDto.getPageSize(),sort));

        Page<ProductDto> product = page.map(product1 -> ProductConvertor.productToProductDto(product1));
        PageableResponse<ProductDto> pageableResponse = Helper.getPageableResponse(product, ProductDto.class);

        return pageableResponse;
    }

    @Override
    public List<ProductDto> searchProducts(String search) {
        List<Product> products = productRepository.findByTitleContaining(search);

        List<ProductDto> productDtos = products.stream().map(product -> ProductConvertor.productToProductDto(product)).toList();

        return productDtos;
    }

    @Override
    public ProductDto createProductWithCategory(ProductDto productDto, String categoryId) {

        // getting category from database
        CategoryDto categoryDto = categoryService.getCategory(categoryId);
        if(categoryDto == null){
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        String productId = UUID.randomUUID().toString();
        productDto.setProductId(productId);

        // productDto to product
        Product product = ProductConvertor.productDtoToProduct(productDto);
        product.setCategory(category);
        product.setLiveStatus(ProductLiveStatus.NOT_LIVE);
        product.setCreatedDate(Helper.getCurrentDate());

        // saving product in database
        Product savedProduct = productRepository.save(product);
        return ProductConvertor.productToProductDto(savedProduct);
    }

    @Override
    public ProductDto updateProductWithCategory(String categoryId, String productId) {
        CategoryDto categoryDto = categoryService.getCategory(categoryId);
        if(categoryDto == null){
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        product.setCategory(category);
        product.setUpdatedDate(Helper.getCurrentDate());

        Product updatedProduct  = productRepository.save(product);

        return ProductConvertor.productToProductDto(updatedProduct);
    }

    @Override
    public PageableResponse<ProductDto> getAllProductsByCategory(String categoryId, PageDto pageDto) {
        CategoryDto categoryDto = categoryService.getCategory(categoryId);
        if(categoryDto == null){
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        Category category = modelMapper.map(categoryDto, Category.class);

        Sort sort = Sort.by(pageDto.getSortBy());
        sort = pageDto.getSortDir().equals("desc") ? sort.descending() : sort.ascending();

        Page<Product> productPage = productRepository.findByCategory(category,PageRequest.of(pageDto.getPageNumber(),pageDto.getPageSize(),sort));

        Page<ProductDto> productDtoPage = productPage.map(ProductConvertor::productToProductDto);

        PageableResponse<ProductDto> pageableResponse = Helper.getPageableResponse(productDtoPage, ProductDto.class);

        return pageableResponse;
    }
}

