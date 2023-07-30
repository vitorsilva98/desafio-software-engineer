package com.wallet.operations.listeners;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.operations.enums.OperationStatus;
import com.wallet.operations.enums.OperationTypes;
import com.wallet.operations.models.OperationModel;
import com.wallet.operations.repositories.OperationsRepository;

@Component
@Transactional
public class OperationFinishedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationFinishedListener.class);

    @Autowired
    private OperationsRepository operationsRepository;
    
    @RabbitListener(queues = "transference_finished.update-operation")
    public void onTransferenceFinished(OperationFinishedEvent event) {
        updateOperation(event.idOperation(), event.status(), OperationTypes.TRANSFERENCE);
    }

    @RabbitListener(queues = "deposit_finished.update-operation")
    public void onDepositFinished(OperationFinishedEvent event) {
        updateOperation(event.idOperation(), event.status(), OperationTypes.DEPOSIT);
    }

    @RabbitListener(queues = "withdrawal_finished.update-operation")
    public void onWithdrawalFinished(OperationFinishedEvent event) {
        updateOperation(event.idOperation(), event.status(), OperationTypes.WITHDRAWAL);
    }

    @RabbitListener(queues = "payment_finished.update-operation")
    public void onPaymentFinished(OperationFinishedEvent event) {
        updateOperation(event.idOperation(), event.status(), OperationTypes.PAYMENT);
    }

    private void updateOperation(UUID id, OperationStatus status, OperationTypes type) {
        LOGGER.info(String.format("[%s %s FINISHED WITH STATUS = %s]", type, id, status));
        OperationModel operationModel = operationsRepository.getReferenceById(id);

        operationModel.finish(status);
        operationsRepository.save(operationModel);
        LOGGER.info(String.format("[OPERATION UPDATED] %s", operationModel));
    }

    private record OperationFinishedEvent(UUID idOperation, OperationStatus status, String userEmail) {}
}
