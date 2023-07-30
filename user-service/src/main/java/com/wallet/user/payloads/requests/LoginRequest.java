package com.wallet.user.payloads.requests;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequest implements Serializable {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @ToString.Exclude
    private String password;
}
