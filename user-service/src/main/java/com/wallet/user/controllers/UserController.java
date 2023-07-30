package com.wallet.user.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.user.payloads.requests.CreateUserRequest;
import com.wallet.user.payloads.responses.CreateUserResponse;
import com.wallet.user.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallet/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@RequestBody @Valid CreateUserRequest request) {
        LOGGER.info(String.format("[POST] /users | endpoint input = %s", request));
        CreateUserResponse response = userService.create(request);
        LOGGER.info(String.format("[POST] /users | endpoint output = %s", response));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
