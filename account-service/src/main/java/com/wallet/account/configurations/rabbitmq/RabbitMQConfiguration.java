package com.wallet.account.configurations.rabbitmq;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Bean
    public Queue queuePaymentAllowed() {
        return new Queue("payments_events.approved");
    }

    @Bean
    public Queue queuePaymentResult() {
        return new Queue("payments_events.result");
    }

    @Bean
    public Queue queueWithdrawalAllowed() {
        return new Queue("withdrawals_events.approved");
    }

    @Bean
    public Queue queueWithdrawalResult() {
        return new Queue("withdrawals_events.result");
    }

    @Bean
    public Queue queueAccountCreated() {
        return new Queue("accounts_events.created");
    }

    @Bean
    public Queue queueUserCreated() {
        return new Queue("users_events.created");
    }
    
    @Bean
    public Queue queueDepositRequested() {
        return new Queue("operations_events.deposit-requested");
    }

    @Bean
    public Queue queueWithdrawalRequested() {
        return new Queue("operations_events.withdrawal-requested");
    }

    @Bean
    public Queue queueTransferenceRequested() {
        return new Queue("operations_events.transference-requested");
    }

    @Bean
    public Queue queuePaymentRequested() {
        return new Queue("operations_events.payment-requested");
    }

    @Bean
    public FanoutExchange depositFanoutExchange() {
        return new FanoutExchange("deposit_finished");
    }

    @Bean
    public FanoutExchange withdrawalFanoutExchange() {
        return new FanoutExchange("withdrawal_finished");
    }

    @Bean
    public FanoutExchange paymentFanoutExchange() {
        return new FanoutExchange("payment_finished");
    }

    @Bean
    public FanoutExchange transferenceFanoutExchange() {
        return new FanoutExchange("transference_finished");
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(
            RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
