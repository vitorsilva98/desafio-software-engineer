package com.wallet.operations.payloads.responses;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.wallet.operations.enums.OperationStatus;
import com.wallet.operations.enums.OperationTypes;
import com.wallet.operations.models.OperationModel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OperationResponse implements Serializable {
    private UUID id;
    private String barCodeBill;
    private BigDecimal amount;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private OperationStatus status;
    private OperationTypes type;
    private String transferredAccount;
    private String bankCodeSent;
    private String accountNumberSent;

    public OperationResponse(OperationModel operationModel) {
        this.id = operationModel.getId();
        this.barCodeBill = operationModel.getBarCodeBill();
        this.amount = operationModel.getAmount();
        this.startDateTime = operationModel.getStartDateTime();
        this.finishDateTime = operationModel.getFinishDateTime();
        this.status = operationModel.getStatus();
        this.type = operationModel.getType();
        this.bankCodeSent = operationModel.getBankCodeSent();
        this.accountNumberSent = operationModel.getAccountNumberSent();
        if (operationModel.getTransferredAccount() != null) {
            this.transferredAccount = operationModel.getTransferredAccount().getAccountNumber();
        }
    }
}
