package org.ead.apigateway.exception;

import org.ead.apigateway.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorDTO> generateException(RestException e){
        ErrorDTO errorDTO = ErrorDTO.builder()
                .error(e.getMsg())
                .status(e.getStatusCode().value())
                .timestamp(new Date())
                .build();
        return new ResponseEntity<>(errorDTO, e.getStatusCode());
    }
}
