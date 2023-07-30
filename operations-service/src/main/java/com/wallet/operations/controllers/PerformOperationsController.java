package com.wallet.operations.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.operations.payloads.requests.DepositRequest;
import com.wallet.operations.payloads.requests.PaymentRequest;
import com.wallet.operations.payloads.requests.TransferenceRequest;
import com.wallet.operations.payloads.requests.WithdrawalRequest;
import com.wallet.operations.payloads.responses.PerformOperationResponse;
import com.wallet.operations.services.OperationsService;
import com.wallet.operations.utils.HttpServletRequestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallet/perform")
public class PerformOperationsController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformOperationsController.class);

    @Autowired
    private OperationsService operationsService;

    @PostMapping("/payment")
    public ResponseEntity<PerformOperationResponse> payment(@RequestBody @Valid PaymentRequest request, HttpServletRequest httpServletRequest) {
        LOGGER.info(String.format("[POST] /payment | endpoint input = %s", request));
        PerformOperationResponse response = operationsService.performPayment(request, 
            HttpServletRequestUtils.getUserIdFromRequest(httpServletRequest), HttpServletRequestUtils.getUserEmailFromRequest(httpServletRequest));
        LOGGER.info(String.format("[POST] /payment | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<PerformOperationResponse> deposit(@RequestBody @Valid DepositRequest request, HttpServletRequest httpServletRequest) {
        LOGGER.info(String.format("[POST] /deposit | endpoint input = %s", request));
        PerformOperationResponse response = operationsService.performDeposit(request, 
            HttpServletRequestUtils.getUserIdFromRequest(httpServletRequest), HttpServletRequestUtils.getUserEmailFromRequest(httpServletRequest));
        LOGGER.info(String.format("[POST] /deposit | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<PerformOperationResponse> withdrawal(@RequestBody @Valid WithdrawalRequest request, HttpServletRequest httpServletRequest) {
        LOGGER.info(String.format("[POST] /withdrawal | endpoint input = %s", request));
        PerformOperationResponse response = operationsService.performWithdrawal(request, 
            HttpServletRequestUtils.getUserIdFromRequest(httpServletRequest), HttpServletRequestUtils.getUserEmailFromRequest(httpServletRequest));
        LOGGER.info(String.format("[POST] /withdrawal | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transference")
    public ResponseEntity<PerformOperationResponse> transference(@RequestBody @Valid TransferenceRequest request, HttpServletRequest httpServletRequest) {
        LOGGER.info(String.format("[POST] /transference | endpoint input = %s", request));
        PerformOperationResponse response = operationsService.performTransference(request, 
            HttpServletRequestUtils.getUserIdFromRequest(httpServletRequest), HttpServletRequestUtils.getUserEmailFromRequest(httpServletRequest));
        LOGGER.info(String.format("[POST] /transference | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }
}
