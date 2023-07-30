package com.wallet.user.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.user.configurations.security.services.TokenService;
import com.wallet.user.payloads.requests.LoginRequest;
import com.wallet.user.payloads.responses.LoginResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallet/auth")
public class AuthenticationController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LOGGER.info(String.format("[POST] /login | endpoint input = %s", request));

        var authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        var authentication = authenticationManager.authenticate(authenticationToken);
        LoginResponse response = tokenService.generateToken(authentication);

        LOGGER.info(String.format("[POST] /login | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }
}
