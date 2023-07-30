package com.wallet.notification.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AccountCreatedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCreatedListener.class);
    
    @RabbitListener(queues = "accounts_events.created")
    public void onAccountCreated(AccountCreatedEvent event) {
        LOGGER.info(String.format("[ACCOUNT %s CREATED] Sending email to %s", event.accountNumber, event.email));
    }

    private record AccountCreatedEvent(String email, String accountNumber) {}
}
