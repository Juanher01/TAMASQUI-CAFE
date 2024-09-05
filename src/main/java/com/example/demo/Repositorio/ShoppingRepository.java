package com.example.demo.Repositorio;

import com.example.demo.Dominio.Item;
import com.example.demo.Dominio.Shopping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingRepository extends JpaRepository<Shopping, Long> {

    //Método personalizado para buscar compras por ID de usuario y estado
    List<Shopping> findByUserIdAndState(Long user_id, String state);

    //Método personalizado para buscar compras paginadas por ID de usuario y conjunto de estados
    Page<Shopping> findByUserIdAndStateIn(Long user_id, Collection<String> state, Pageable pageable);

    // Método personalizado para buscar una compra por ID de usuario y ID de compra
    Optional<Shopping> findByUserIdAndId(Long user_id, Long id);
}
