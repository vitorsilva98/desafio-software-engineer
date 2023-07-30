package com.wallet.account.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.account.payloads.requests.UpdateBlockStatusRequest;
import com.wallet.account.payloads.responses.AccountResponse;
import com.wallet.account.services.AccountService;
import com.wallet.account.utils.HttpServletRequestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallet/account")
public class AccountController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @GetMapping("/getByLoggedUser")
    public ResponseEntity<AccountResponse> getByUser(HttpServletRequest httpServletRequest) {
        UUID userId = HttpServletRequestUtils.getUserIdFromRequest(httpServletRequest);
        LOGGER.info(String.format("[GET] /account/getByLoggedUser | endpoint input = %s", userId));
        AccountResponse response = accountService.getByLoggedUser(userId);
        LOGGER.info(String.format("[GET] /account/getByLoggedUser | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AccountResponse>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        LOGGER.info(String.format("[GET] /account | endpoint input = %s", pageable));
        Page<AccountResponse> response = accountService.getAll(pageable);
        LOGGER.info(String.format("[GET] /account | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> updateBlockStatus(@RequestBody @Valid UpdateBlockStatusRequest request, 
            @PathVariable("accountNumber") String accountNumber) {
        LOGGER.info(String.format("[GET] /account/{accountNumber} | endpoint input = %s, %s", accountNumber, request));
        AccountResponse response = accountService.updateBlockedStatus(accountNumber, request);
        LOGGER.info(String.format("[GET] /account/{accountNumber} | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }
}
