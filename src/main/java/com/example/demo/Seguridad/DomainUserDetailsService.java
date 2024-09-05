package com.example.demo.Seguridad;

import com.example.demo.Dominio.Authority;
import com.example.demo.Repositorio.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);


    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //metodo para cargar el usuario por su username
    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);

        // Convertir el nombre de usuario a minúsculas
        String lowercaselogin = login.toLowerCase();
        // Buscar el usuario en el repositorio y crear un objeto de Spring Security
        return userRepository.findOneWithAuthoritiesByLogin(lowercaselogin)
                .map(user -> createSpringSecurityUser(lowercaselogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User" + lowercaselogin + "was not found in the database"));
    }


    // Método para crear un usuario de Spring Security con las autoridades del usuario
    private User createSpringSecurityUser(String lowercaselogin, com.example.demo.Dominio.User user) {
        List<GrantedAuthority> grantedAuthorities = user
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new User(user.getLogin(), user.getPassword(), grantedAuthorities);

    }
}
