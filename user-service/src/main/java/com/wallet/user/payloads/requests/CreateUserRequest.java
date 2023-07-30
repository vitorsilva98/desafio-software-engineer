package com.wallet.user.payloads.requests;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateUserRequest implements Serializable {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 11, max = 14)
    private String document;
    @NotBlank
    @ToString.Exclude
    private String password;
}
