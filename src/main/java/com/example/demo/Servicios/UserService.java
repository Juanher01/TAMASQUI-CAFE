package com.example.demo.Servicios;


import com.example.demo.Dominio.Authority;
import com.example.demo.Dominio.User;
import com.example.demo.Repositorio.AuthorityRepository;
import com.example.demo.Repositorio.UserRepository;
import com.example.demo.Seguridad.AuthoritiesConstants;
import com.example.demo.Seguridad.SecurityUtils;
import com.example.demo.Servicios.dto.RegisterUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    // Generador de números aleatorios seguros
    private static final SecureRandom SECURE_RANDOM;

    // Inicializa el generador de números aleatorios seguros
    static{
        SECURE_RANDOM = new SecureRandom();
        SECURE_RANDOM.nextBytes(new byte[64]);
    }

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Registra un nuevo usuario en la base de datos
    public User registerUser(RegisterUserDTO userDTO){
        // Verifica si el nombre de usuario ya está en uso
        userRepository.findOneByLogin((userDTO.getLogin().toLowerCase()))
                .ifPresent(user -> {
                    throw new RuntimeException("Login name already used");
                });
        // Crea una nueva instancia de User y establece sus propiedades
        User user = new User();
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setLogin(userDTO.getLogin());
        user.setName(userDTO.getName());
        user.setPassword(encryptedPassword);
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        // Asigna las autoridades al usuario
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        user.setAuthorities(authorities);
        // Guarda el usuario en la base de datos
        userRepository.save(user);
        // Registra la creación del usuario
        log.debug("Created information for user: {}", user.toString());
        return user;
    }

    // Recupera el usuario actual con sus autoridades
    public Optional<User> getUserWithAuthorities(){
        return SecurityUtils.getCurrentLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);

    }

}
