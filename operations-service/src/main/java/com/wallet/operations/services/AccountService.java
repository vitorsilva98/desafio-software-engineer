package com.wallet.operations.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wallet.operations.configurations.exception.BusinessRuleException;
import com.wallet.operations.configurations.exception.EntityNotFoundException;
import com.wallet.operations.models.AccountModel;
import com.wallet.operations.repositories.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public AccountModel validateAccount(UUID userId) {
        AccountModel accountModel = accountRepository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        return validateBlockAccount(accountModel, "Conta está bloqueada");
    }

    public AccountModel validateAccount(String accountNumber) {
        AccountModel accountModel = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new EntityNotFoundException("Conta a ser transferido o valor não encontrada"));
        
        return validateBlockAccount(accountModel, "Conta a ser transferido o valor está bloqueada");
    }

    private AccountModel validateBlockAccount(AccountModel accountModel, String message) {
        if (accountModel.isBlocked()) {
            throw new BusinessRuleException(message);
        } 

        return accountModel;
    }
}
