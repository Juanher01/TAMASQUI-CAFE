package com.example.demo.Seguridad;

import com.example.demo.Configuracion.ApplicationProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORIZATION_KEY = "auth";

    private final Key key;

    private final JwtParser jwtparser;

    private final long tokenValidityInMilliSeconds;

    public TokenProvider(ApplicationProperties applicationProperties) {
        byte[] keyByte;
        String secret = applicationProperties.getSecurity().getAuthentication().getJwt().getBase64secret();
        if (!ObjectUtils.isEmpty(secret)){
            log.debug("Using a Base64.encoded JWT secret key ");
        } else {
            log.warn("Warning: the JWT key used is not Base64-encoded");
            secret = applicationProperties.getSecurity().getAuthentication().getJwt().getSecret();

        }
        keyByte = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyByte);
        this.jwtparser = Jwts.parserBuilder().setSigningKey(key).build();
        this.tokenValidityInMilliSeconds = 1000 * applicationProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
    }

    public String createToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityInMilliSeconds);

        return  Jwts
                .builder()
                .setSubject(authentication.getName())
                .claim(AUTHORIZATION_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token){
        Claims claims = jwtparser.parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORIZATION_KEY).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "",authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    public boolean validateToken(String authToken){
        try {
            jwtparser.parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            log.info("Invalid Jwt token.");
            log.info("Invalid jwt token trace.");
        }
        return false;
    }

}
