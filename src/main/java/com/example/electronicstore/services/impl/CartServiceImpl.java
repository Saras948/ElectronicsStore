package com.example.electronicstore.services.impl;

import com.example.electronicstore.dtos.*;
import com.example.electronicstore.entities.*;
import com.example.electronicstore.exception.ProductNotActiveException;
import com.example.electronicstore.exception.ResourceNotFoundException;
import com.example.electronicstore.repositories.CartItemRepository;
import com.example.electronicstore.repositories.CartRepository;
import com.example.electronicstore.services.CartService;
import com.example.electronicstore.services.ProductService;
import com.example.electronicstore.services.UserService;
import com.example.electronicstore.utilities.Helper;
import com.example.electronicstore.utilities.ProductConvertor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest addItemToCartRequest) {

        UserDto userDto = userService.getUserById(userId);
        if(userDto == null){
            throw new RuntimeException("User not found with id: "+userId+"!!");
        }

        User user = modelMapper.map(userDto, User.class);
        Cart cart = null;
        try{
            cart = cartRepository.findByUser(user).get();
            cart.setUpdatedDate(Helper.getCurrentDate());
        }
        catch(NoSuchElementException e){
            String cartId = UUID.randomUUID().toString();
            cart = Cart.builder()
                    .cartId(cartId)
                    .user(user)
                    .build();
            cart.setCreatedDate(Helper.getCurrentDate());
        }

        ProductDto productDto = productService.getProductById(addItemToCartRequest.getProductId());
        if(productDto == null){
            throw new ResourceNotFoundException("Product not found with id: "+addItemToCartRequest.getProductId());
        }
        Product product = ProductConvertor.productDtoToProduct(productDto);

        // updating the quantity of the product
        int currentQuantity = product.getQuantity();
        if(product.getLiveStatus().equals(ProductLiveStatus.NOT_LIVE)){
            throw new ProductNotActiveException("Product is not live!!");
        }
        if(currentQuantity < addItemToCartRequest.getQuantity()){
            throw new ResourceNotFoundException("Only "+currentQuantity+" item is available only!!");
        }
        int updatedQuantity = currentQuantity - addItemToCartRequest.getQuantity();
        product.setQuantity(updatedQuantity);
        double totalPrice = cart.getTotalPrice() + product.getProductPrice() * addItemToCartRequest.getQuantity();

        AtomicReference<Boolean> isProductAlreadyInCart = new AtomicReference<>(false);
        List<CartItem> items = cart.getCartItems();
        List<CartItem> updateItems = null;
        if(items != null && items.size() > 0) {
            updateItems = items.stream().map(item -> {
                if (item.getProduct().getProductId().equals(product.getProductId())) {
                    item.setQuantity(item.getQuantity() + addItemToCartRequest.getQuantity());
                    item.setTotalPrice(item.getTotalPrice() + (product.getProductPrice() *
                            addItemToCartRequest.getQuantity()));
                    item.setUpdatedDate(Helper.getCurrentDate());
                    isProductAlreadyInCart.set(true);
                }
                return item;
            }).collect(Collectors.toList());
            items = updateItems;
        }
        if(!isProductAlreadyInCart.get()){
            if(items == null){
                items = new ArrayList<>();
            }
            CartItem cartItem = CartItem.builder()
                    .product(product)
                    .totalPrice(product.getProductPrice() * addItemToCartRequest.getQuantity())
                    .quantity(addItemToCartRequest.getQuantity())
                    .build();
            cartItem.setCart(cart);
            cartItem.setCreatedDate(Helper.getCurrentDate());

            items.add(cartItem);
        }
        cart.setCartItems(items);
        cart.setTotalQuantity(items.size());
        cart.setTotalPrice(totalPrice);

        ProductDto updatedProductDto = ProductConvertor.productToProductDto(product);
        //updating the product
        productService.updateProduct(updatedProductDto, product.getProductId());
        // updating the cart
        Cart savedCart = cartRepository.save(cart);
        return cartToCartDto(savedCart);
    }

    @Override
    public void removeItemFromCart(String userId, int cartItemId) {

        UserDto userDto = userService.getUserById(userId);
        if (userDto == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId + "!!");
        }
        User user = modelMapper.map(userDto, User.class);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId + "!!"));
        List<CartItem> items = cart.getCartItems();
        List<CartItem> updatedCartItems = items.stream().filter(item -> item.getCartItemId() != cartItemId)
                .collect(Collectors.toList());

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId + "!!"));

        cartItemRepository.delete(cartItem);
        double totalPrice = cart.getTotalPrice() - cartItem.getTotalPrice();
        cart.setTotalPrice(totalPrice);
        cart.setTotalQuantity(updatedCartItems.size());
        cart.setCartItems(updatedCartItems);
        cart.setUpdatedDate(Helper.getCurrentDate());
        cart.setTotalQuantity(updatedCartItems.size());

        ProductDto productDto = productService.getProductById(cartItem.getProduct().getProductId());
        Product product = ProductConvertor.productDtoToProduct(productDto);
        product.setQuantity(product.getQuantity() + cartItem.getQuantity());
        ProductDto updatedProductDto = ProductConvertor.productToProductDto(product);
        productService.updateProduct(updatedProductDto, product.getProductId());
        cartRepository.save(cart);
//        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String userId) {
        UserDto userDto = userService.getUserById(userId);
        if(userDto == null){
            throw new RuntimeException("User not found with id: "+userId+"!!");
        }
        User user = modelMapper.map(userDto, User.class);
        Cart  cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: "+userId+"!!"));
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.stream().forEach(cartItem -> {
            ProductDto productDto = productService.getProductById(cartItem.getProduct().getProductId());
            Product product = ProductConvertor.productDtoToProduct(productDto);
            product.setQuantity(product.getQuantity() + cartItem.getQuantity());
            product.setUpdatedDate(Helper.getCurrentDate());
            ProductDto updatedProductDto = ProductConvertor.productToProductDto(product);
            productService.updateProduct(updatedProductDto, product.getProductId());
            cartItemRepository.delete(cartItem);
        });
        cart.getCartItems().clear();
        cart.setUpdatedDate(Helper.getCurrentDate());
        cart.setTotalPrice(0);
        cart.setTotalQuantity(0);
        cartRepository.save(cart);
    }

    @Override
    public CartDto getCartByUser(String userId) {
        UserDto userDto = userService.getUserById(userId);
        if(userDto == null){
            throw new RuntimeException("User not found with id: "+userId+"!!");
        }
        User user = modelMapper.map(userDto, User.class);
        Cart  cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: "+userId+"!!"));
//        return modelMapper.map(cart, CartDto.class);
        return cartToCartDto(cart);
    }

    private CartDto cartToCartDto(Cart cart)
    {
        CartDto cartDto = CartDto.builder()
                .cartId(cart.getCartId())
                .user(modelMapper.map(cart.getUser(), UserDto.class))
                .totalPrice(cart.getTotalPrice())
                .totalQuantity(cart.getTotalQuantity())
                .build();
        List<CartItem> cartItems = cart.getCartItems();
        List<CartItemDto> cartItemDtos = new ArrayList<>();
        for(CartItem cartItem : cartItems) {
            ProductDto productDto = ProductConvertor.productToProductDto(cartItem.getProduct());
            CartDto cartDto1 = CartDto.builder()
                    .cartId(cart.getCartId())
                    .totalPrice(cart.getTotalPrice())
                    .user(modelMapper.map(cart.getUser(), UserDto.class))
                    .totalQuantity(cart.getTotalQuantity())
                    .build();
            CartItemDto cartItemDto = CartItemDto.builder()
                    .cartItemId(cartItem.getCartItemId())
                    .product(productDto)
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .cartDto(cartDto1)
                    .build();
            cartItemDtos.add(cartItemDto);
        }
        cartDto.setCartItems(cartItemDtos);
        return cartDto;
    }

    private Cart cartDtoToCart(CartDto cartDto)
    {
        Cart cart = Cart.builder()
                .cartId(cartDto.getCartId())
                .user(modelMapper.map(cartDto.getUser(), User.class))
                .totalPrice(cartDto.getTotalPrice())
                .totalQuantity(cartDto.getTotalQuantity())
                .build();
        List<CartItemDto> cartItemDtos = cartDto.getCartItems();
        List<CartItem> cartItems = new ArrayList<>();
        for(CartItemDto cartItemDto : cartItemDtos) {
            Product product = ProductConvertor.productDtoToProduct(cartItemDto.getProduct());
            Cart cart1 = Cart.builder()
                    .cartId(cartDto.getCartId())
                    .totalPrice(cartDto.getTotalPrice())
                    .user(modelMapper.map(cartDto.getUser(), User.class))
                    .totalQuantity(cartDto.getTotalQuantity())
                    .build();
            CartItem cartItem = CartItem.builder()
                    .cartItemId(cartItemDto.getCartItemId())
                    .product(product)
                    .quantity(cartItemDto.getQuantity())
                    .totalPrice(cartItemDto.getTotalPrice())
                    .cart(cart1)
                    .build();
            cartItems.add(cartItem);
        }
        cart.setCartItems(cartItems);
        return cart;
    }
}
