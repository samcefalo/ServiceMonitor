package me.samcefalo.servicemonitor.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceStopException extends ServiceException {

    private String message;

}
