package com.example.demo.Seguridad;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//CONFIGURA EL FILTRO JWT PARA LA SEGURIDAD
public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    // Proveedor de tokens para manejar los tokens JWT
    private final TokenProvider tokenProvider;

    // Constructor para inyectar el proveedor de tokens
    public JWTConfigurer(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // Método para configurar la seguridad HTTP
    @Override
    public void configure(HttpSecurity builder) {
        // Crear una instancia del filtro JWT con el proveedor de tokens
        JWTFilter jwtFilter = new JWTFilter(tokenProvider);
        // Agregar el filtro JWT antes del filtro de autenticación de usuario y contraseña
        builder.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
