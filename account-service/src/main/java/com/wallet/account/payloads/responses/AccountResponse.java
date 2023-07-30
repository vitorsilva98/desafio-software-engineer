package com.wallet.account.payloads.responses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.wallet.account.models.AccountModel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountResponse implements Serializable {
    private UUID id;
    private String accountNumber;
    private BigDecimal balance;
    private boolean blocked;
    private String userEmail;

    public AccountResponse(AccountModel accountModel) {
        this.id = accountModel.getId();
        this.accountNumber = accountModel.getAccountNumber();
        this.balance = accountModel.getBalance();
        this.blocked = accountModel.getBlocked();
        this.userEmail = accountModel.getUser().getEmail();
    }
}
