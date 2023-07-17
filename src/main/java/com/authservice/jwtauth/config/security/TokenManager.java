package com.authservice.jwtauth.config.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenManager implements Serializable {

    @Value("${jwt.secret}")
    private String secret;
    private static final long serialVersionUID = 7008375124389347049L;
    private final long expirationMillis = System.currentTimeMillis() + (5 * 60 * 60 * 1000);

    public String generateJwtToken(Authentication authentication) {
        // get authenticated user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        final Date newExp = new Date(expirationMillis);
        Map<String, Object> claims = new HashMap<>();

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(newExp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return token;
    }

    public String refreshJwtToken(String token) {
        final Date newExp = new Date(expirationMillis);
        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        claims.setIssuedAt(new Date());
        claims.setExpiration(newExp);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public Boolean validateJwtToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Boolean isTokenExpired = claims.getExpiration().before(new Date());
        return (username.equals(userDetails.getUsername()) && !isTokenExpired);
    }

    public String getUsernameFromToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
