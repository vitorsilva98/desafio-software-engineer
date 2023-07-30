package com.wallet.account.listeners;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.account.models.AccountModel;
import com.wallet.account.repositories.AccountRepository;
import com.wallet.account.repositories.UserRepository;

@Component
public class UserCreatedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatedListener.class);

    @Autowired
	private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Random random = new Random();

    @RabbitListener(queues = "users_events.created")
    @Transactional
    public void onUserCreated(UserCreatedEvent event) {
        LOGGER.info(String.format("[USER %s CREATED] Creating account", event.id()));

        AccountModel accountModel = new AccountModel();
        accountModel.setUser(userRepository.getReferenceById(event.id()));
        accountModel.setAccountNumber(generateRandomAccountNumber());
        accountModel.setBlocked(Boolean.FALSE);
        accountModel.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountModel);
        LOGGER.info(String.format("[ACCOUNT CREATED] = %s", accountModel));

        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent(event.email(), accountModel.getAccountNumber());
        rabbitTemplate.convertAndSend("accounts_events.created", accountCreatedEvent);
        LOGGER.info(String.format("Sent %s to accounts_events.created", accountCreatedEvent));
    }

    private String generateRandomAccountNumber() {
        return String.valueOf(random.nextInt(100000000));
    }

    private record UserCreatedEvent(UUID id, String email) {}

    private record AccountCreatedEvent(String email, String accountNumber) {}
}
