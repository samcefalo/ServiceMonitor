package me.samcefalo.servicemonitor.dtos;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Builder
public class ResponseError {

    private HttpStatus statusCode;
    private Date timestamp;
    private String message;
    private String description;

}
