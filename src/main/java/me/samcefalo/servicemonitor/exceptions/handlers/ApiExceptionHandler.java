package me.samcefalo.servicemonitor.exceptions.handlers;

import me.samcefalo.servicemonitor.dtos.ResponseError;
import me.samcefalo.servicemonitor.exceptions.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseError> handleServiceException(ServiceException serviceException, WebRequest request) {

        ResponseError responseError = ResponseError.builder()
                .description(request.getDescription(false))
                .message(serviceException.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(responseError, new HttpHeaders(), responseError.getStatusCode());
    }

}
