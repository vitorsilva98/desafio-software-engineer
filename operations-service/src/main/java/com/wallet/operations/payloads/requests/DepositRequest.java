package com.wallet.operations.payloads.requests;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DepositRequest implements Serializable {
    @DecimalMin("0.01")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;
}
