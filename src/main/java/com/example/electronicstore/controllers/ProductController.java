package com.example.electronicstore.controllers;


import com.example.electronicstore.dtos.*;
import com.example.electronicstore.services.FileService;
import com.example.electronicstore.services.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Value("${product.image.path}")
    private String productImagePath;

    //create product
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto){
        ProductDto  product = productService.createProduct(productDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    //update product
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto,@PathVariable String productId){
        ProductDto product = productService.updateProduct(productDto, productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    //delete product
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId){
        productService.deleteProduct(productId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Product deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get product by id
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String productId){
        ProductDto product = productService.getProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    //get all products
    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProduct(@RequestBody PageDto pageDto)
    {
       PageableResponse<ProductDto> products = productService.getAllProducts(pageDto);
        return new ResponseEntity<>(products,HttpStatus.OK);
    }


    //get all products : live
    @GetMapping("/live/{isLive}")
    public ResponseEntity<PageableResponse<ProductDto>> getAllProductsLive(@PathVariable String isLive,@RequestBody PageDto pageDto){
        PageableResponse<ProductDto> products = productService.getAllProductsLive(isLive, pageDto);
        return new ResponseEntity<>(products,HttpStatus.OK);
    }
    //search products
    @GetMapping("/search/{search}")
    public ResponseEntity<List<ProductDto>> searchProducts(@PathVariable String search){
        List<ProductDto> products = productService.searchProducts(search);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadImage(@PathVariable("productId") String productId,
                                                     @RequestParam("imageName") MultipartFile image) throws IOException {
        String fileName = fileService.uploadFile(image,productImagePath);
        ProductDto productDto = productService.getProductById(productId);
        productDto.setProductImageName(fileName);
        ProductDto product = productService.updateProduct(productDto,productId);
        ImageResponse response = ImageResponse.builder()
                .message("Image uploaded successfully!!")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/image/{productId}")
    public void getProductImage(@PathVariable("productId") String productId, HttpServletResponse response) throws IOException {

        String imageName = productService.getProductById(productId).getProductImageName();
        InputStream resource = fileService.getResource(imageName,productImagePath);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        StreamUtils.copy(resource,response.getOutputStream());
    }

}
