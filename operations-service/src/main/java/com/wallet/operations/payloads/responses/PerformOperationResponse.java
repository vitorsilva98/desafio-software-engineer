package com.wallet.operations.payloads.responses;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class PerformOperationResponse implements Serializable {
    private UUID idOperation;
    private String message;
}
