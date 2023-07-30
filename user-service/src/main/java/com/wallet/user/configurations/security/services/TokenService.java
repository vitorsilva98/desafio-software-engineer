package com.wallet.user.configurations.security.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wallet.user.models.UserModel;
import com.wallet.user.payloads.responses.LoginResponse;

@Service
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    
    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    @Value("${api.security.token.expires}")
    private Integer expires;

    public LoginResponse generateToken(Authentication authentication) {
        try {
            UserModel userModel = (UserModel) authentication.getPrincipal();

            Instant expirationDate = getExpirationDate();

            String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(userModel.getUsername())
                .withExpiresAt(expirationDate)
                .withClaim("id", userModel.getId().toString())
                .sign(Algorithm.HMAC256(secret));

            return new LoginResponse(token, expirationDate.toEpochMilli());
        } catch (Exception e) {
            LOGGER.info(String.format("Error generating token = %s", e.getMessage()));
            throw new RuntimeException("Error generating token");
        }
    }

    public String getUserIdClaim(String tokenJWT) {
        try {
            return verifyJwt(tokenJWT)
                .getClaim("id")
                .toString();
        } catch (Exception e) {
            LOGGER.info(String.format("Error decoding token = %s", e.getMessage()));
            throw new RuntimeException("Error decoding token");
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            return verifyJwt(tokenJWT)
                .getSubject();
        } catch (Exception e) {
            LOGGER.info(String.format("Error decoding token = %s", e.getMessage()));
            throw new RuntimeException("Error decoding token");
        }
    }

    private DecodedJWT verifyJwt(String tokenJWT) {
         return JWT.require(Algorithm.HMAC256(secret))
            .withIssuer(issuer)
            .build()
            .verify(tokenJWT);
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusMinutes(expires).toInstant(ZoneOffset.of("-03:00"));
    }
}
