package com.wallet.notification.listeners;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.wallet.notification.enums.OperationStatus;

@Component
public class OperationsListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationsListener.class);
    
    @RabbitListener(queues = "transference_finished.send-notification")
    public void onTransferenceFinished(OperationFinishedEvent event) {
        LOGGER.info(String.format("[TRANSFERENCE %s FINISHED WITH STATUS = %s] Sending email to %s", 
            event.idOperation(), event.status(), event.userEmail()));
    }

    @RabbitListener(queues = "deposit_finished.send-notification")
    public void onDepositFinished(OperationFinishedEvent event) {
        LOGGER.info(String.format("[DEPOSIT %s FINISHED WITH STATUS = %s] Sending email to %s", 
            event.idOperation(), event.status(), event.userEmail()));
    }

    @RabbitListener(queues = "withdrawal_finished.send-notification")
    public void onWithdrawalFinished(OperationFinishedEvent event) {
        LOGGER.info(String.format("[WITHDRAWAL %s FINISHED WITH STATUS = %s] Sending email to %s", 
            event.idOperation(), event.status(), event.userEmail()));
    }

    @RabbitListener(queues = "payment_finished.send-notification")
    public void onPaymentFinished(OperationFinishedEvent event) {
        LOGGER.info(String.format("[PAYMENT %s FINISHED WITH STATUS = %s] Sending email to %s", 
            event.idOperation(), event.status(), event.userEmail()));
    }

    private record OperationFinishedEvent(UUID idOperation, OperationStatus status, String userEmail) {}
}
