package com.wallet.payment.listeners;

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
public class PaymentListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentListener.class);
    private Random random = new Random();

    @Autowired
	private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "payments_events.approved")
    public void onPaymentApproved(PaymentApprovedEvent event) {
        LOGGER.info(String.format("[PAYMENT OF BILL %s APPROVED BY %s]", event.barCodeBill(), event.idAccount()));
        PaymentResultEvent paymentResponseEvent = new PaymentResultEvent(event.idOperation(), event.idAccount(), event.userEmail(), event.amount(), event.barCodeBill(), pay(event.barCodeBill()));
        rabbitTemplate.convertAndSend("payments_events.result", paymentResponseEvent);
        LOGGER.info(String.format("Sent %s to payments_events.result", paymentResponseEvent));
    }

    private boolean pay(String barCodeBill) {
        LOGGER.info(String.format("[PAYMENT BILL... %s]", barCodeBill));
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

    private record PaymentApprovedEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String barCodeBill) {}

    private record PaymentResultEvent(UUID idOperation, UUID idAccount, String userEmail, BigDecimal amount, String barCodeBill,  boolean success) {}
}
