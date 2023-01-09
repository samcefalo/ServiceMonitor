package me.samcefalo.servicemonitor.controllers;

import me.samcefalo.servicemonitor.dtos.Response;
import me.samcefalo.servicemonitor.exceptions.ServiceException;
import me.samcefalo.servicemonitor.services.WindowsServiceExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/api/service")
public class ServiceMonitorController {

    @Autowired
    private WindowsServiceExecutor windowsServiceExecutor;

    @GetMapping(value = "/status/{serviceName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getServiceStatus(@PathVariable String serviceName) throws ServiceException, IOException {
        Response response = windowsServiceExecutor.getServiceStatus(serviceName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/start/{serviceName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> startService(@PathVariable String serviceName) throws ServiceException, IOException {
        Response response = windowsServiceExecutor.startService(serviceName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/stop/{serviceName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> stopService(@PathVariable String serviceName) throws ServiceException, IOException {
        Response response = windowsServiceExecutor.stopService(serviceName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/restart/{serviceName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> restartService(@PathVariable String serviceName) throws ServiceException, IOException {
        Response response = windowsServiceExecutor.restartService(serviceName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
