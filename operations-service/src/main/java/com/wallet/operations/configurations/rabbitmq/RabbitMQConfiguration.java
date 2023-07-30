package com.wallet.operations.configurations.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
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
    public Queue queueDepositRequested() {
        return new Queue("operations_events.deposit-requested");
    }

    @Bean
    public Queue queueWithdrawRequested() {
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
    public Queue queueTransferenceFinished() {
        return new Queue("transference_finished.update-operation");
    }

    @Bean
    public Binding bindingTransferenceFinished() {
        Queue queue = new Queue("transference_finished.update-operation");
        FanoutExchange fanoutExchange = new FanoutExchange("transference_finished");
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Queue queueDepositFinished() {
        return new Queue("deposit_finished.update-operation");
    }

    @Bean
    public Binding bindingDepositFinished() {
        Queue queue = new Queue("deposit_finished.update-operation");
        FanoutExchange fanoutExchange = new FanoutExchange("deposit_finished");
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Queue queueWithdrawalFinished() {
        return new Queue("withdrawal_finished.update-operation");
    }

    @Bean
    public Binding bindingWithdrawalFinished() {
        Queue queue = new Queue("withdrawal_finished.update-operation");
        FanoutExchange fanoutExchange = new FanoutExchange("withdrawal_finished");
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public Queue queuePaymentFinished() {
        return new Queue("payment_finished.update-operation");
    }

    @Bean
    public Binding bindingPaymentFinished() {
        Queue queue = new Queue("payment_finished.update-operation");
        FanoutExchange fanoutExchange = new FanoutExchange("payment_finished");
        return BindingBuilder.bind(queue).to(fanoutExchange);
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
