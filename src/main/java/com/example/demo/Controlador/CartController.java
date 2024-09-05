package com.example.demo.Controlador;

import com.example.demo.Dominio.Cart;
import com.example.demo.Dominio.Shopping;
import com.example.demo.Servicios.CartService;
import com.example.demo.Servicios.ShoppingService;
import com.example.demo.Servicios.dto.CreateCarDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://127.0.0.1:5501")
public class CartController {

    private final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    private final ShoppingService shoppingService;

    public CartController(CartService cartService, ShoppingService shoppingService) {
        this.cartService = cartService;
        this.shoppingService = shoppingService;
    }

    // api para crear un nuevo carrito de compras
    @PostMapping("/cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> createCart(@RequestBody CreateCarDTO createCarDTO){
        return new ResponseEntity<>(cartService.createCart(createCarDTO), HttpStatus.CREATED);
    }

    // api para obtener el carrito de compras del ususario
    @GetMapping("/cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Shopping> getCart(){
        return  ResponseEntity.ok().body(shoppingService.getOneShopping()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    // api para eliminar el carrito de compras
    @DeleteMapping("/cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCart(/*@PathVariable("shop-id") Long shopId*/) {
        shoppingService.deleteShop();
        return ResponseEntity.noContent().build();

    }

    // api para eliminar un ítem del carrito de compras
    @DeleteMapping("/cart/item/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") Long cartId) {
        cartService.deleteCartItem(cartId);
        return ResponseEntity.noContent().build();

    }
}

