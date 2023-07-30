package com.wallet.account.payloads.events;

import java.util.UUID;

import com.wallet.account.enums.OperationStatus;

public record OperationFinishedEvent(UUID idOperation, OperationStatus status, String userEmail) {}
