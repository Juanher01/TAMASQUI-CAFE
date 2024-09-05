package com.example.demo.Servicios;

import com.example.demo.Configuracion.ApplicationProperties;
import com.example.demo.Dominio.Shopping;
import com.example.demo.Dominio.User;
import com.example.demo.Repositorio.ShoppingRepository;
import com.example.demo.Servicios.dto.CheckoutDTO;
import com.fasterxml.jackson.core.Base64Variant;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingService {
    private final Logger log = LoggerFactory.getLogger(ShoppingService.class);

    private final ShoppingRepository shoppingRepository;

    private final UserService userService;

    private final ApplicationProperties applicationProperties;

    public ShoppingService(ShoppingRepository shoppingRepository, UserService userService, ApplicationProperties applicationProperties) {
        this.shoppingRepository = shoppingRepository;
        this.userService = userService;
        this.applicationProperties = applicationProperties;
    }

    //Guarda una compra en la base de datos
    public Shopping save(Shopping shopping){
        return shoppingRepository.save(shopping);
    }

    //Recupera todas las compras de un usuario en un estado especifico
    @Transactional(readOnly = true)
    public List<Shopping> getShoppingUser(Long userId, String state){
        return shoppingRepository.findByUserIdAndState(userId, state);
    }

    //Recupera todas las compras paginadas de un usuario con ciertos estados
    @Transactional(readOnly = true)
    public Page<Shopping> getShoppingUser(Pageable pageable) {
        Optional<User> userOpt = userService.getUserWithAuthorities();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return shoppingRepository.findByUserIdAndStateIn(user.getId(), List.of("PAID", "APPROVED", "SHIPMENT"), pageable);
        } else {
            return Page.empty();
        }
    }

    //Recupera todas las compras
    @Transactional(readOnly = true)
    public Page<Shopping> getAllShopping(Pageable pageable) {
        return shoppingRepository.findAll(pageable);
    }

    //Valida si existe un carrito de compras pendiente para un usuario
    @Transactional(readOnly = true)
    public Optional<Shopping> validateShoppingCartUser(Long userId){
        return this.getShoppingUser(userId, "PENDIENTE").stream().limit(1).findFirst();
    }

    //Recupera una compra pendiente para el ususario actual
    @Transactional(readOnly = true)
    public Optional<Shopping> getOneShopping(){
        return userService.getUserWithAuthorities()
                .flatMap(user -> this.validateShoppingCartUser(user.getId()));
    }

    //Recupera una compra especifica de un  usuario
    @Transactional(readOnly = true)
    public Optional<Shopping> getOneShoppingUser(Long id){
        return userService.getUserWithAuthorities()
                .flatMap(user -> shoppingRepository.findByUserIdAndId(user.getId(), id));
    }

    // Procesa el checkout de una compra y crea un pago
    public Preference checkout(CheckoutDTO checkoutDTO) {
        log.debug("Request to checkout shop: {}", checkoutDTO);
        return this.createPayment(this.updateShopping(checkoutDTO));
    }

    //Actualiza el estado de una compra segun el DTO de checkout
    public Shopping updateShopping(CheckoutDTO checkoutDTO){
        return shoppingRepository.findById(checkoutDTO.getShopId()).map(shopping -> {
            switch (shopping.getState()){
                case "CHECKOUT":
                    shopping.setPaymentId(checkoutDTO.getPaymentId());
                    break;
                case "PENDIENTE":
                    shopping.setDate(LocalDateTime.now());

            }
            shopping.setState(checkoutDTO.getStatus());
            return shoppingRepository.save(shopping);

        }).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe this id for the checkout"));
    }

    //Elimina el carrito de compras del usuario actual
    @Transactional
    public void deleteShop(){
        userService.getUserWithAuthorities()
                .flatMap(user -> this.validateShoppingCartUser(user.getId()))
                .ifPresent(shop -> shoppingRepository.deleteById(shop.getId()));
    }

    //Elimina una compra especifica
    @Transactional
    public void deleteShop(Shopping shopId){
        shoppingRepository.delete(shopId);
    }

    //Actualiza el precio total de la compra
    @Transactional
    public void updateTotalPrice(Shopping shopping, BigDecimal totalPrice){
        shopping.setTotalPrice(shopping.getTotalPrice().subtract(totalPrice));
        shoppingRepository.save(shopping);
    }

    //Procesa el webhook de un pago
    public Payment webhookCheckout(String id){
        log.debug("Get payment id: {}", id);
        MercadoPagoConfig.setAccessToken(applicationProperties.getMercadoPago().getAccessToken());
        PaymentClient client = new PaymentClient();
        try {
            Payment payment = client.get(Long.parseLong(id));
            this.updateShopping(new CheckoutDTO(Long.parseLong(payment.getExternalReference()), "PAID", payment.getId()));
            return  payment;
        }catch (MPException | MPApiException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }
    }


    //Crea un pago utilizando la api de mercado pago
    private Preference createPayment(Shopping shopping) {
        MercadoPagoConfig.setAccessToken("TEST-8071710669033047-052215-a24d0bce36b77f01a1599ff7ad34062d-1823124441");


        List<PreferenceItemRequest> items = new ArrayList<>();

        shopping.getCarts().forEach(cart -> {
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(cart.getItem().getName())
                    .quantity(cart.getAmount())
                    .unitPrice(cart.getUnitePrice())
                    .currencyId("COP")
                    .build();
            items.add(itemRequest);
        });

        PreferenceClient client = new PreferenceClient();

        try {
            return client.create(PreferenceRequest.builder()
                    .items(items)
                    .notificationUrl("http://localhost:8080/api/webcheckout")
                    .backUrls(PreferenceBackUrlsRequest.builder()
                            .success("http://localhost:8080/api/success")
                            .failure("http://localhost:8080/api/failure")
                            .pending("http://localhost:8080/api/pending")
                            .build())
                            .payer(PreferencePayerRequest.builder()
                                    .name(shopping.getUser().getName())
                                    .email(shopping.getUser().getEmail())
                                    .build())
                    .build());
        } catch (MPException | MPApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }
    }

}
