package me.samcefalo.servicemonitor.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceRestartException extends ServiceException {

    private String message;

}
