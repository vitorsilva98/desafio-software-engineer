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
public class OperationsRequestedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationsRequestedListener.class);

    @Autowired
	private RabbitTemplate rabbitTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @RabbitListener(queues = "operations_events.transference-requested")
    public void onTransferenceRequested(TransferenceRequestedEvent event) {
        AccountModel accountModel = getAccount(event.idAccount());
        AccountModel transferredAccountModel = getAccount(event.idTransferredAccount());
        LOGGER.info(String.format("[TRANSFENCE OF %s REQUESTED FROM ACCOUNT %s TO ACCOUNT %s]", event.amount(), accountModel, transferredAccountModel));

        OperationFinishedEvent transferenceFinishedEvent = null;
        if (!hasBalance(accountModel.getBalance(), event.amount())) {
            LOGGER.info("[TRANSFERENCE DENIED] Insufficient balance");
            transferenceFinishedEvent = new OperationFinishedEvent(event.idOperation(), OperationStatus.REFUSED, event.userEmail());         
        } else {
            accountModel.setBalance(accountModel.getBalance().subtract(event.amount()));
            transferredAccountModel.setBalance(transferredAccountModel.getBalance().add(event.amount()));

            LOGGER.info(String.format("[TRANSFERENCE FINISHED] New balance = %s", accountModel.getBalance()));
            transferenceFinishedEvent = new OperationFinishedEvent(event.idOperation(), OperationStatus.DONE, event.userEmail());
        }

        rabbitTemplate.convertAndSend("transference_finished", "", transferenceFinishedEvent);
        LOGGER.info(String.format("Sent %s to transference_finished", transferenceFinishedEvent));
    }

    @RabbitListener(queues = "operations_events.withdrawal-requested")
    public void onWithdrawalRequested(WithdrawalRequestedEvent event) {
        AccountModel accountModel = getAccount(event.idAccount());
        LOGGER.info(String.format("[WITHDRAWAL OF %s REQUESTED FROM ACCOUNT %s TO BANK %s AND ACCOUNT %s]", event.amount(), accountModel, event.bankCode(), event.accountNumber()));

        if (!hasBalance(accountModel.getBalance(), event.amount())) {
            LOGGER.info("[WITHDRAWAL DENIED] Insufficient balance");
            OperationFinishedEvent withdrawalFinishedEvent = new OperationFinishedEvent(event.idOperation(), OperationStatus.REFUSED, event.userEmail());
            rabbitTemplate.convertAndSend("withdrawal_finished", "", withdrawalFinishedEvent);
            LOGGER.info(String.format("Sent %s to withdrawal_finished", withdrawalFinishedEvent));
        } else {
            LOGGER.info("[WITHDRAWAL ALLOWED]");
            accountModel.setBalance(accountModel.getBalance().subtract(event.amount()));
            rabbitTemplate.convertAndSend("withdrawals_events.approved", event);
            LOGGER.info(String.format("Sent %s to withdrawals_events.approved", event));
        }
    }

    @RabbitListener(queues = "operations_events.deposit-requested")
    public void onDepositRequested(DepositRequestedEvent event) {
        AccountModel accountModel = getAccount(event.idAccount());
        LOGGER.info(String.format("[DEPOSIT OF %s REQUESTED FOR ACCOUNT %s]", event.amount(), accountModel));

        accountModel.setBalance(accountModel.getBalance().add(event.amount()));
        accountRepository.save(accountModel);
        LOGGER.info(String.format("[DEPOSIT FINISHED] New balance = %s", accountModel.getBalance()));

        OperationFinishedEvent depositFinishedEvent = new OperationFinishedEvent(event.idOperation(), OperationStatus.DONE, event.userEmail());
        rabbitTemplate.convertAndSend("deposit_finished", "", depositFinishedEvent);
        LOGGER.info(String.format("Sent %s to deposit_finished", depositFinishedEvent));
    }

    @RabbitListener(queues = "operations_events.payment-requested")
    public void onPaymentRequested(PaymentRequestedEvent event) {
        AccountModel accountModel = getAccount(event.idAccount());
        LOGGER.info(String.format("[PAYMENT OF %s REQUESTED FROM ACCOUNT %s TO BAR CODE BILL %s]", event.amount(), accountModel, event.barCodeBill()));

        if (!hasBalance(accountModel.getBalance(), event.amount())) {
            LOGGER.info("[PAYMENT DENIED] Insufficient balance");
            OperationFinishedEvent paymentFinishedEvent = new OperationFinishedEvent(event.idOperation(), OperationStatus.REFUSED, event.userEmail());
            rabbitTemplate.convertAndSend("payment_finished", "", paymentFinishedEvent);
            LOGGER.info(String.format("Sent %s to payment_finished", paymentFinishedEvent));
        } else {
            LOGGER.info("[PAYMENT ALLOWED]");
            accountModel.setBalance(accountModel.getBalance().subtract(event.amount()));
            rabbitTemplate.convertAndSend("payments_events.approved", event);
            LOGGER.info(String.format("Sent %s to payments_events.approved", event));
        }
    }

    private AccountModel getAccount(UUID id) {
        return accountRepository.getReferenceById(id);
    }

    private boolean hasBalance(BigDecimal balance, BigDecimal amount) {
        return balance.compareTo(amount) > -1;
    }

    private record DepositRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount) {}

    private record TransferenceRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, UUID idTransferredAccount) {}

    private record PaymentRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String barCodeBill) {}
    
    private record WithdrawalRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String bankCode, String accountNumber) {}
}
