package com.example.demo.Repositorio;

import com.example.demo.Dominio.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Método personalizado para buscar un usuario por su nombre de usuario
    Optional<User> findOneByLogin(String login);

    // Método personalizado para buscar un usuario con sus roles (authorities) cargados mediante un grafo de entidad
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByLogin(String login);
}

