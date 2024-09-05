package com.example.demo.Servicios;

import com.example.demo.Dominio.Cart;
import com.example.demo.Dominio.Item;
import com.example.demo.Dominio.Shopping;
import com.example.demo.Dominio.User;
import com.example.demo.Repositorio.CartRepository;
import com.example.demo.Repositorio.ItemRepository;
import com.example.demo.Repositorio.ShoppingRepository;
import com.example.demo.Repositorio.UserRepository;
import com.example.demo.Seguridad.SecurityUtils;
import com.example.demo.Servicios.dto.CreateCarDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;


    private final UserService userService;

    private final ItemService itemService;

    private final ShoppingService shoppingService;

    public CartService(CartRepository cartRepository, ShoppingRepository shoppingRepository, ItemRepository itemRepository, UserRepository userRepository, UserService userService, ItemService itemService, ShoppingService shoppingService) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.shoppingService = shoppingService;
    }

    //metodo para crear un nuevo carrito de compras
    public Cart createCart(CreateCarDTO carDTO){
        log.debug("create cart {}", carDTO);
        Cart cart = new Cart();
        Shopping shopping = new Shopping();
        userService.getUserWithAuthorities().ifPresent(shopping::setUser);
        shoppingService.validateShoppingCartUser(shopping.getUser().getId())
                        .ifPresentOrElse(existingShopping -> this.validateCartItem(carDTO.getProductId(), existingShopping.getId())  //valid item in shopping cart
                                        .ifPresentOrElse(cart::getCart,
                                () -> itemService.getOneItem(carDTO.getProductId()).map(item -> {
                                    cart.setItem(item);
                                    cart.setAmount(carDTO.getAmount());
                                    cart.setUnitePrice(cart.getItem().getPrice());
                                    cart.setTotalPrice(cart.getUnitePrice().multiply(new BigDecimal(cart.getAmount())));
                                    shopping.setId(existingShopping.getId());
                                    shopping.setTotalPrice(existingShopping.getTotalPrice().add(cart.getTotalPrice()));
                                    shopping.setState(existingShopping.getState());
                                    cart.setShopping(shoppingService.save(shopping));
                                    return item;
                                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        () -> itemService.getOneItem(carDTO.getProductId()).map(item -> {
                            cart.setItem(item);
                            cart.setAmount(carDTO.getAmount());
                            cart.setUnitePrice(cart.getItem().getPrice());
                            cart.setTotalPrice(cart.getUnitePrice().multiply(new BigDecimal(cart.getAmount())));
                            shopping.setTotalPrice(cart.getTotalPrice());
                            shopping.setState("PENDIENTE");
                            cart.setShopping(shoppingService.save(shopping));
                            return item;
                        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                        );
        return cartRepository.save(cart);
    }

    //metodo para validar un elemento del carrito
    @Transactional(readOnly = true)
    public Optional<Cart> validateCartItem(Long itemId, Long shoppingId) {
        return  cartRepository.findByItemIdAndShoppingId(itemId, shoppingId);
    }

    //metodo para eliminar un elemento del carrito
    public void deleteCartItem(Long cartId) {
        cartRepository.findById(cartId)
                .ifPresent(cart -> {
                    if (cart.getShopping().getCarts().size() > 1) {
                        Shopping shopping = cart.getShopping();
                        shopping.getCarts().removeIf(c -> c.getId().equals(cart.getId()));
                        shoppingService.updateTotalPrice(shopping, cart.getTotalPrice());
                        cartRepository.delete(cart);
                    } else  {
                        shoppingService.deleteShop(cart.getShopping());
                    }
                });

    }

}
