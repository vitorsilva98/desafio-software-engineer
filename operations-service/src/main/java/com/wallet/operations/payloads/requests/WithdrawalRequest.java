package com.wallet.operations.payloads.requests;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WithdrawalRequest implements Serializable {
    @DecimalMin("0.01")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;
    @NotBlank
    @Size(min = 3, max = 3)
    private String bankCode;
    @NotBlank
    @Size(min = 1, max = 10)
    private String accountNumber;
}
