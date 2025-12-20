package org.verse.metabird.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(AuthenticationFailedException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleAuthFailure(
            AuthenticationFailedException ex
    ) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "AUTHENTICATION_FAILED",
                                "message", ex.getMessage()
                        ))
        );
    }

    @ExceptionHandler(SalesforceAuthException.class)
    public ResponseEntity<Map<String, String>> handleSalesforceAuth(
            SalesforceAuthException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", ex.getCode(),
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "AUTHENTICATION_FAILED",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "USER_ALREADY_EXISTS",
                        "message", ex.getMessage()
                ));
    }

}
