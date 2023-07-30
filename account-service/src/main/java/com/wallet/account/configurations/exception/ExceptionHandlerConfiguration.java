package com.wallet.account.configurations.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerConfiguration {

    private static final String LOG_MESSAGE = "An exception occurred = %s";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerConfiguration.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handlerUserNotFoundResponse() {
        Error error = new Error("Email ou senha inválidos");
        LOGGER.info(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Error> handlerUserDisabledResponse() {
        Error error = new Error("Usuário desabilitado");
        LOGGER.info(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InputError> handlerInputErrorValidationResponse(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getFieldErrors();
        InputError inputError = new InputError("Parâmetros de entrada inválidos", errors.stream().map(InputErrorDetails::new).toList());
        LOGGER.info(String.format(LOG_MESSAGE, inputError));
        return ResponseEntity.badRequest().body(inputError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> handlerEntityNotFoundResponse(EntityNotFoundException ex) {
        Error error = new Error(ex.getMessage());
        LOGGER.info(String.format(LOG_MESSAGE, error));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private record Error(String message) {}
    
    private record InputError(String message, List<InputErrorDetails> erros) {}

    private record InputErrorDetails(String field, String message) {
        public InputErrorDetails(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
