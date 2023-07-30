package com.wallet.operations.services;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wallet.operations.configurations.exception.BusinessRuleException;
import com.wallet.operations.configurations.exception.EntityNotFoundException;
import com.wallet.operations.enums.OperationTypes;
import com.wallet.operations.models.AccountModel;
import com.wallet.operations.models.OperationModel;
import com.wallet.operations.payloads.requests.DepositRequest;
import com.wallet.operations.payloads.requests.PaymentRequest;
import com.wallet.operations.payloads.requests.TransferenceRequest;
import com.wallet.operations.payloads.requests.WithdrawalRequest;
import com.wallet.operations.payloads.responses.OperationResponse;
import com.wallet.operations.payloads.responses.PerformOperationResponse;
import com.wallet.operations.repositories.OperationsRepository;

@Service
public class OperationsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationsService.class);
    private static final String RESPONSE_MESSAGE = "Executando operação";
    private static final String OPERATION_CREATED_LOG = "[OPERATION CREATED] = %s";

    @Autowired
    private OperationsRepository operationsRepository;

    @Autowired
	private RabbitTemplate rabbitTemplate;

    @Autowired
    private AccountService accountService;

    public OperationResponse getById(UUID id) {
        OperationModel operationModel = operationsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Operação não encontrada"));
        return new OperationResponse(operationModel);
    }

    public Page<OperationResponse> getAllByUser(UUID userId, Pageable pageable) {
        return operationsRepository.findAllByAccountUserId(userId, pageable).map(OperationResponse::new);
    }

    public PerformOperationResponse performDeposit(DepositRequest request, UUID userId, String userEmail) {
        AccountModel accountModel = accountService.validateAccount(userId);
        OperationModel operationModel = OperationModel.start(accountModel, request.getAmount(), OperationTypes.DEPOSIT, null, null, null, null);
        operationsRepository.save(operationModel);
        LOGGER.info(String.format(OPERATION_CREATED_LOG, operationModel));

        DepositRequestedEvent event = new DepositRequestedEvent(operationModel.getId(), accountModel.getId(), userEmail, operationModel.getAmount());
        rabbitTemplate.convertAndSend("operations_events.deposit-requested", event);
        LOGGER.info(String.format("Sent %s to operations_events.deposit-requested", event));

        return new PerformOperationResponse(operationModel.getId(), RESPONSE_MESSAGE);
    }

    public PerformOperationResponse performPayment(PaymentRequest request, UUID userId, String userEmail) {
        AccountModel accountModel = accountService.validateAccount(userId);
        OperationModel operationModel = OperationModel.start(accountModel, request.getAmount(), OperationTypes.PAYMENT, request.getBarCodeBill(), null, null, null);
        operationsRepository.save(operationModel);
        LOGGER.info(String.format(OPERATION_CREATED_LOG, operationModel));

        PaymentRequestedEvent event = new PaymentRequestedEvent(operationModel.getId(), accountModel.getId(), userEmail, operationModel.getAmount(), operationModel.getBarCodeBill());
        rabbitTemplate.convertAndSend("operations_events.payment-requested", event);
        LOGGER.info(String.format("Sent %s to operations_events.payment-requested", event));

        return new PerformOperationResponse(operationModel.getId(), RESPONSE_MESSAGE);
    }

    public PerformOperationResponse performTransference(TransferenceRequest request, UUID userId, String userEmail) {
        AccountModel accountModel = accountService.validateAccount(userId);
        AccountModel accountTransferredModel = accountService.validateAccount(request.getAccountNumber());

        if (accountModel.equals(accountTransferredModel)) {
            throw new BusinessRuleException("Operação inválida");
        }

        OperationModel operationModel = OperationModel.start(accountModel, request.getAmount(), OperationTypes.TRANSFERENCE, null, accountTransferredModel, null, null);
        operationsRepository.save(operationModel);
        LOGGER.info(String.format(OPERATION_CREATED_LOG, operationModel));

        TransferenceRequestedEvent event = new TransferenceRequestedEvent(operationModel.getId(), accountModel.getId(), userEmail, operationModel.getAmount(), accountTransferredModel.getId());
        rabbitTemplate.convertAndSend("operations_events.transference-requested", event);
        LOGGER.info(String.format("Sent %s to operations_events.transference-requested", event));

        return new PerformOperationResponse(operationModel.getId(), RESPONSE_MESSAGE);
    }

    public PerformOperationResponse performWithdrawal(WithdrawalRequest request, UUID userId, String userEmail) {
        AccountModel accountModel = accountService.validateAccount(userId);
        OperationModel operationModel = OperationModel.start(accountModel, request.getAmount(), OperationTypes.WITHDRAWAL, null, null, request.getBankCode(), request.getAccountNumber());
        operationsRepository.save(operationModel);
        LOGGER.info(String.format(OPERATION_CREATED_LOG, operationModel));

        WithdrawalRequestedEvent event = new WithdrawalRequestedEvent(operationModel.getId(), accountModel.getId(), userEmail, operationModel.getAmount(), request.getBankCode(), request.getAccountNumber());
        rabbitTemplate.convertAndSend("operations_events.withdrawal-requested", event);
        LOGGER.info(String.format("Sent %s to operations_events.withdrawal-requested", event));

        return new PerformOperationResponse(operationModel.getId(), RESPONSE_MESSAGE);
    }

    private record DepositRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount) {}

    private record PaymentRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String barCodeBill) {}
    
    private record TransferenceRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, UUID idTransferredAccount) {}

    private record WithdrawalRequestedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String bankCode, String accountNumber) {}
}
