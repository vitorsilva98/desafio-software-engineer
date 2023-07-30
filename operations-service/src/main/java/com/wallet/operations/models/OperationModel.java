package com.wallet.operations.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.wallet.operations.enums.OperationStatus;
import com.wallet.operations.enums.OperationTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.ToString;

@Table(name = "operations")
@Entity
@Getter
@ToString
public class OperationModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 100, nullable = true)
    private String barCodeBill;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "finish_date_time", nullable = true)
    private LocalDateTime finishDateTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private OperationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    private OperationTypes type;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountModel account;

    @ManyToOne
    @JoinColumn(name = "transferred_account", nullable = true)
    private AccountModel transferredAccount;

    @Column(length = 3, nullable = true)
    private String bankCodeSent;

    @Column(length = 10, nullable = true)
    private String accountNumberSent;

    public static OperationModel start(AccountModel account, BigDecimal amount, OperationTypes type, String barCodeBill, AccountModel transferredAccount, String bankCodeSent, String accountNumberSent) {
        OperationModel operationModel = new OperationModel();
        operationModel.type = type;
        operationModel.amount = amount;
        operationModel.account = account;
        operationModel.status = OperationStatus.PENDING;
        operationModel.startDateTime = LocalDateTime.now();

        if (type == OperationTypes.PAYMENT) {
            operationModel.barCodeBill = barCodeBill;
        } else if (type == OperationTypes.TRANSFERENCE) {
            operationModel.transferredAccount = transferredAccount;
        } else if (type == OperationTypes.WITHDRAWAL) {
            operationModel.bankCodeSent = bankCodeSent;
            operationModel.accountNumberSent = accountNumberSent;
        }

        return operationModel;
    }

    public void finish(OperationStatus status) {
        this.status = status;
        this.finishDateTime = LocalDateTime.now();
    }
}
