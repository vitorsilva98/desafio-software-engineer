package com.wallet.account.payloads.requests;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateBlockStatusRequest implements Serializable {
    @NotNull
    private boolean blocked;
}
