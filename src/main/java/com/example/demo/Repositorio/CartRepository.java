package com.example.demo.Repositorio;


import com.example.demo.Dominio.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    //Método personalizado para encontrar un Cart basado en item_id y shopping_id
    Optional<Cart> findByItemIdAndShoppingId(Long item_id, Long shopping_id);
}

