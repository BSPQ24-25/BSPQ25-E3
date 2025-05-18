package com.student_loan.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import java.util.Base64;

@Component
public class JwtUtil {
    // Not the best practice to put the secret key in the code
    // We will do it this way for now to ease the process
    private static final String SECRET_KEY = "c29tZXZlcnlzZWN1cmVhbmRsb25nYmFzZTY0a2V5MTIzNDU2";
    private static final long EXPIRATION_TIME = 86400000; // 1 day in miliseconds

    private final Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}