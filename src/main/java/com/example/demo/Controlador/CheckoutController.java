package com.example.demo.Controlador;

import com.example.demo.Configuracion.ApplicationProperties;
import com.example.demo.Dominio.Shopping;
import com.example.demo.Seguridad.AuthoritiesConstants;
import com.example.demo.Servicios.ShoppingService;
import com.example.demo.Servicios.dto.CheckoutDTO;
import com.example.demo.Servicios.dto.ResponsePayDTO;
import com.google.gson.Gson;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://127.0.0.1:5501")
public class CheckoutController {

    private final Logger log = LoggerFactory.getLogger(CheckoutController.class);
    private final ShoppingService shoppingService;

    private final ApplicationProperties applicationProperties;

    public CheckoutController(ShoppingService shoppingService, ApplicationProperties applicationProperties) {
        this.shoppingService = shoppingService;
        this.applicationProperties = applicationProperties;
    }

    //api iniciar proceso de compra
    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<Preference> checkout(@RequestBody CheckoutDTO checkoutDTO) {
        log.debug("REST request to checkout shop: {}", checkoutDTO);

        return new ResponseEntity<>(shoppingService.checkout(checkoutDTO), HttpStatus.CREATED);
    }

    //api para que se actualicen pedidos--- solo admin
    @PutMapping("/admin/order")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Shopping> updateOrder(@RequestBody CheckoutDTO checkoutDTO){
        log.debug("Rest request to create payment: {}", checkoutDTO);
        return ResponseEntity.ok(shoppingService.updateShopping(checkoutDTO));
    }

    //api para manejar respuestas de pago en linea
    @PostMapping("/webcheckout")
    public ResponseEntity<Payment> webCheckout(@RequestBody String response) {
        log.debug("Rest request to create payment: {}", response);
        ResponsePayDTO responsPay = new Gson().fromJson(response, ResponsePayDTO.class);
        if (responsPay.getType().equals("payment")){
            return ResponseEntity.ok().body(shoppingService.webhookCheckout(responsPay.getData().getId()));
        } else {
            return ResponseEntity.ok().build();
        }
    }

    //api para obtener los pedidos de forma paginada---- admin
    @GetMapping("/admin/orders")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public  ResponseEntity<List<Shopping>> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(shoppingService.getAllShopping(pageable).getContent(), HttpStatus.OK);
    }

    //api para que los usuarios obtengan sus pedidos
    @GetMapping("/user/orders")
    @PreAuthorize("isAuthenticated()")
    public  ResponseEntity<List<Shopping>> getAllOrdersUser(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Shopping> orders = shoppingService.getShoppingUser(pageable);
        return new ResponseEntity<>(orders.getContent(), HttpStatus.OK);
    }
    //api para obtener un pedido especifico por su id
    @GetMapping("/user/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Shopping> getAllOrderUser(@PathVariable Long id){
        return shoppingService.getOneShoppingUser(id)
                .map(ResponseEntity::ok)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
