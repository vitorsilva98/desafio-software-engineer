package com.wallet.operations.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.operations.payloads.responses.OperationResponse;
import com.wallet.operations.services.OperationsService;
import com.wallet.operations.utils.HttpServletRequestUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/wallet/operations")
public class ConsultOperationsController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultOperationsController.class);

    @Autowired
    private OperationsService operationsService;

    @GetMapping("/{id}")
    public ResponseEntity<OperationResponse> getById(@PathVariable UUID id) {
        LOGGER.info(String.format("[GET] /operations/{id} | endpoint input = %s", id));
        OperationResponse response = operationsService.getById(id);
        LOGGER.info(String.format("[GET] /operations/{id} | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OperationResponse>> getAllByUser(
            @PageableDefault(sort = {"startDateTime"}, direction = Sort.Direction.DESC, size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
        LOGGER.info(String.format("[GET] /operations | endpoint input = %s", pageable));
        Page<OperationResponse> response = operationsService.getAllByUser(HttpServletRequestUtils.getUserIdFromRequest(httpServletRequest), pageable);
        LOGGER.info(String.format("[GET] /operations | endpoint output = %s", response));
        return ResponseEntity.ok(response);
    }
}
