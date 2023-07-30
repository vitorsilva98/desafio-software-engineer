package com.wallet.user.payloads.responses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CreateUserResponse implements Serializable {
    private String message;
}
