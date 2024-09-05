package com.example.demo.Controlador;

import com.example.demo.Dominio.User;
import com.example.demo.Seguridad.TokenProvider;
import com.example.demo.Servicios.UserService;
import com.example.demo.Servicios.dto.LoginDTO;
import com.example.demo.Servicios.dto.RegisterUserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://127.0.0.1:5501")
public class AccountController {

    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final UserService userService;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public AccountController(UserService userService, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    //api para la autenticacion de usuarios
    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@RequestBody LoginDTO login){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        return new ResponseEntity<>(new JWTToken(jwt), HttpStatus.OK);
    }

    //api para registrar una nueva cuentea de usuario
    @PostMapping("/register")
    @CrossOrigin("http://127.0.0.1:5501")
    public ResponseEntity<User> registerAccount(@RequestBody RegisterUserDTO userDTO) {
        if(isPasswordLength(userDTO.getPassword())) {
            throw new RuntimeException(HttpStatus.BAD_REQUEST.getReasonPhrase() + " Incorrect password");

        }
        User user = userService.registerUser(userDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    //api para obtener la información de la cuenta del ususario autenticado
    @GetMapping("/account")
    @PreAuthorize("isAuthenticated()")
    public User getAccount()
    {
        return userService.getUserWithAuthorities().orElseThrow(() -> new RuntimeException("User could no be found"));
    }

    //Metodo para verificar si la longitud de la contraseña es valida
    private static  boolean isPasswordLength(String password){
        return (StringUtils.isEmpty(password) || password.length() < RegisterUserDTO.PASSWORD_MIN_LENGTH || password.length() > RegisterUserDTO.PASSWORD_MAX_LENGTH);
    }

    //clase para obtener el token jwt
    static class JWTToken{
        private String token;


        @JsonProperty("token")
        public String getToken() {
            return token;
        }

        public JWTToken(String token) {
            this.token = token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
