package com.wallet.account.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.account.models.AccountModel;

public interface AccountRepository extends JpaRepository<AccountModel, UUID> {
    Optional<AccountModel> findByUserId(UUID idUser);
    Optional<AccountModel> findByAccountNumber(String accountNumber);
}
