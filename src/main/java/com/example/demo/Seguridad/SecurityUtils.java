package com.example.demo.Seguridad;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;


public class SecurityUtils {

    // Método estático para obtener el nombre de usuario actualmente autenticado
    public static Optional<String> getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    // Método privado para extraer el nombre de usuario de la autenticación
    private static String extractPrincipal(Authentication authetication){
        if(authetication == null) {
            return null;
        } else if(authetication.getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails) authetication.getPrincipal();
            return userDetails.getUsername();
        } else if(authetication.getPrincipal() instanceof String){
            return (String) authetication.getPrincipal();

        }
        return null;
    }
}
