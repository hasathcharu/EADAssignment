package org.adbms.orderplacement.exception;

import org.adbms.orderplacement.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorDTO> generateException(RestException e){
        ErrorDTO errorDTO = ErrorDTO.builder()
                .error(e.getReason())
                .status(e.getStatusCode().value())
                .timestamp(new Date())
                .build();
        return new ResponseEntity<ErrorDTO>(errorDTO, e.getStatusCode());
    }
}
