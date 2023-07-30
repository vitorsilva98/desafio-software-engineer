package com.wallet.operations.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.operations.models.OperationModel;

public interface OperationsRepository extends JpaRepository<OperationModel, UUID> {
    Page<OperationModel> findAllByAccountUserId(UUID userId, Pageable pageable);
}
