package com.wallet.account.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.account.configurations.exception.EntityNotFoundException;
import com.wallet.account.models.AccountModel;
import com.wallet.account.payloads.requests.UpdateBlockStatusRequest;
import com.wallet.account.payloads.responses.AccountResponse;
import com.wallet.account.repositories.AccountRepository;

@Service
public class AccountService {

    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Conta não encontrada";

    @Autowired
    private AccountRepository accountRepository;

    public AccountResponse getByLoggedUser(UUID userId) {
        AccountModel accountModel = accountRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE));
        return new AccountResponse(accountModel);
    }
    
    public Page<AccountResponse> getAll(Pageable pageable) {
        return accountRepository.findAll(pageable).map(AccountResponse::new);
    }

    @Transactional
    public AccountResponse updateBlockedStatus(String accountNumber, UpdateBlockStatusRequest request) {
        AccountModel accountModel = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new EntityNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE));
        accountModel.setBlocked(request.isBlocked());
        return new AccountResponse(accountModel);
    }
}
