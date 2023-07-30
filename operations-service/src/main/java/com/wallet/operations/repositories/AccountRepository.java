package com.wallet.operations.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.operations.models.AccountModel;

public interface AccountRepository extends JpaRepository<AccountModel, UUID> {
    Optional<AccountModel> findByUserId(UUID userId);
    Optional<AccountModel> findByAccountNumber(String accountNumber);
}
