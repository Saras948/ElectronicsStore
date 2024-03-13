package com.example.electronicstore.utilities;

import com.example.electronicstore.dtos.CategoryDto;
import com.example.electronicstore.dtos.ProductDto;
import com.example.electronicstore.entities.Category;
import com.example.electronicstore.entities.Product;
import com.example.electronicstore.entities.ProductLiveStatus;
import com.example.electronicstore.entities.StockStatus;
import org.modelmapper.ModelMapper;

public class ProductConvertor {
    public static Product productDtoToProduct(ProductDto productDto) {
        Product product = Product.builder()
                .productId(productDto.getProductId())
                .title(productDto.getTitle())
                .description(productDto.getDescription())
                .productPrice(productDto.getProductPrice())
                .quantity(Integer.parseInt(productDto.getQuantity()))
                .liveStatus(productDto.getLiveStatus()!=null && productDto.getLiveStatus().equalsIgnoreCase("LIVE") ? ProductLiveStatus.LIVE : ProductLiveStatus.NOT_LIVE)
                .stockStatus(productDto.getStockStatus().equalsIgnoreCase("IN_STOCK") ? StockStatus.IN_STOCK : StockStatus.OUT_OF_STOCK)
                .productImageName(productDto.getProductImageName())
                .category(productDto.getCategoryDto()!=null ? new ModelMapper().map(productDto.getCategoryDto(), Category.class) : null)
                .build();

        return product;
    }

    public static ProductDto productToProductDto(Product product){
        ProductDto productDto = ProductDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .productPrice(product.getProductPrice())
                .quantity(String.valueOf(product.getQuantity()))
                .liveStatus(product.getLiveStatus().toString())
                .stockStatus(product.getStockStatus().toString())
                .productImageName(product.getProductImageName())
                .categoryDto(product.getCategory()!=null ? new ModelMapper().map(product.getCategory(), CategoryDto.class) : null)
                .build();

        return productDto;
    }
}
