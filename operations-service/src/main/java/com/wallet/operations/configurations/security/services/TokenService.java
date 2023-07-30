package com.wallet.operations.configurations.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    
    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    @Value("${api.security.token.expires}")
    private Integer expires;

    public String getUserIdClaim(String tokenJWT) {
        try {
            return verifyJwt(tokenJWT)
                .getClaim("id")
                .asString();
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
}
