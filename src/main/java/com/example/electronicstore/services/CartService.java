package com.example.electronicstore.services;

import com.example.electronicstore.dtos.AddItemToCartRequest;
import com.example.electronicstore.dtos.CartDto;

public interface CartService {

    //add items to cart:
    //case1: cart for user is not available : we will create a new cart for user and add items to it
    //case2: cart for user is available : we will add items to it

    CartDto addItemToCart(String userId, AddItemToCartRequest addItemToCartRequest);

    //remove items from cart:
    void removeItemFromCart(String userId, int cartItemId);

    //remove all items from cart
    void clearCart(String userId);

    CartDto getCartByUser(String userId);
}
