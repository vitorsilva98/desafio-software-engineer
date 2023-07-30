package com.wallet.account.listeners;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.account.enums.OperationStatus;
import com.wallet.account.models.AccountModel;
import com.wallet.account.payloads.events.OperationFinishedEvent;
import com.wallet.account.repositories.AccountRepository;

@Component
@Transactional
public class WithdrawalListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawalListener.class);

    @Autowired
	private RabbitTemplate rabbitTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @RabbitListener(queues = "withdrawals_events.result")
    public void onWithdrawalResult(WithdrawalResultEvent event) {
        LOGGER.info(String.format("[WITHDRAWAL %s %s]", event.idOperation(), event.success() ? "SUCCESS" : "FAILED"));

        OperationStatus status = OperationStatus.DONE;
        if (!event.success()) {
            AccountModel accountModel = accountRepository.getReferenceById(event.idAccount());
            accountModel.setBalance(accountModel.getBalance().add(event.amount()));

            status = OperationStatus.CANCELED;
        }

        OperationFinishedEvent withdrawalFinishedEvent = new OperationFinishedEvent(event.idOperation(), status, event.userEmail());
        rabbitTemplate.convertAndSend("withdrawal_finished", "", withdrawalFinishedEvent);
        LOGGER.info(String.format("Sent %s to withdrawal_finished", withdrawalFinishedEvent));
    }

    private record WithdrawalResultEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String bankCode, String accountNumber, String message, boolean success) {}
}
