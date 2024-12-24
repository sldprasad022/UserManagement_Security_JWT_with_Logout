package com.techpixe.serviceImpl;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.security.jwt.expirationTime}")
    private long expirationTime;

    // Generate a JWT token with email and role as claims
    public String generateToken(String email, String role) 
    {
        return Jwts.builder()
                .setSubject(email) // Use email as the subject
                .claim("role", role) // Add role as a custom claim
                .setIssuedAt(new Date())
                .setExpiration(calculateExpirationTime())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract all claims from the token
    public Claims extractClaims(String token) 
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract the email (subject) from the token
    public String extractEmail(String token)
    {
        return extractClaims(token).getSubject();
    }

    // Extract the role from the token
    public String extractRole(String token) 
    {
        return extractClaims(token).get("role", String.class);
    }

    // Validate the token
    public boolean isTokenValid(String token)
    {
        try 
        {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        }
        catch (Exception e) 
        {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    // Calculate expiration time for the token
    private Date calculateExpirationTime()
    {
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    // Get the signing key from the secret
    private SecretKey getSigningKey() 
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

