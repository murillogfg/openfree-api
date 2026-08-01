package com.openfree_api.modules.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secret;
    private final long expiration;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration}") long expiration
    ) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String gerarToken(UserDetails userDetails) {

        Date agora = new Date();
        Date expiracao = new Date(
                agora.getTime() + expiration
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getSigningKey())
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(
                token,
                Claims::getSubject
        );
    }

    public Date extrairExpiracao(String token) {
        return extrairClaim(
                token,
                Claims::getExpiration
        );
    }

    public boolean tokenValido(
            String token,
            UserDetails userDetails
    ) {
        String email = extrairEmail(token);

        return email.equals(userDetails.getUsername())
                && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extrairExpiracao(token)
                .before(new Date());
    }

    private <T> T extrairClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        Claims claims = extrairTodasClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}