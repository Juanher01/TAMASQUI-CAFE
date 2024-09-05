package com.example.demo.Configuracion;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({"com.example.demo.Repositorio"})
public class DatabaseConfiguration {
}
