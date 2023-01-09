package me.samcefalo.servicemonitor.services;

import me.samcefalo.servicemonitor.dtos.Response;
import me.samcefalo.servicemonitor.exceptions.ServiceException;

import java.io.IOException;

public interface ServiceExecutor {

    Response getServiceStatus(String serviceName) throws IOException, ServiceException;

    Response restartService(String serviceName) throws IOException, ServiceException;

    Response startService(String serviceName) throws IOException, ServiceException;

    Response stopService(String serviceName) throws IOException, ServiceException;

    boolean serviceExist(String serviceName) throws IOException;
}
