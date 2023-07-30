package com.wallet.withdrawal.listeners;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawalListener.class);
    private Random random = new Random();

    @Autowired
	private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "withdrawals_events.approved")
    public void onWithdrawalApproved(WithdrawalApprovedEvent event) {
        LOGGER.info(String.format("[WITHDRAWAL REQUESTED BY %s]", event.accountNumber()));
        WithdrawalResultEvent withdrawalResultEvent = new WithdrawalResultEvent(event.idOperation(), event.idAccount(), event.userEmail(), event.amount(), 
            event.bankCode(), event.accountNumber(), withdrawal(event.bankCode(), event.accountNumber()));
        rabbitTemplate.convertAndSend("withdrawals_events.result", withdrawalResultEvent);
        LOGGER.info(String.format("Sent %s to withdrawals_events.result", withdrawalResultEvent));
    }

    private boolean withdrawal(String bankCode, String accountNumber) {
        LOGGER.info(String.format("[SENDING TO... %s, %s]", bankCode, accountNumber));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOGGER.error("Error simulating delay");
            e.printStackTrace();
        }
        boolean success = random.nextInt(100) > 50;
        LOGGER.info(String.format("[PAYMENT %s]", success ? "SUCCESS" : "FAILED"));
        return success;
    }
    
    private record WithdrawalApprovedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String bankCode, String accountNumber) {}

    private record WithdrawalResultEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String bankCode, String accountNumber, boolean success) {}
}
