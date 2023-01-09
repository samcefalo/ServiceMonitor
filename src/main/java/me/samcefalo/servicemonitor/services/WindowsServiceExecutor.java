package me.samcefalo.servicemonitor.services;

import me.samcefalo.servicemonitor.dtos.Response;
import me.samcefalo.servicemonitor.dtos.enums.ServiceStatus;
import me.samcefalo.servicemonitor.exceptions.ServiceException;
import me.samcefalo.servicemonitor.exceptions.ServiceRestartException;
import me.samcefalo.servicemonitor.exceptions.ServiceStartException;
import me.samcefalo.servicemonitor.exceptions.ServiceStopException;
import me.samcefalo.servicemonitor.utils.Log;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class WindowsServiceExecutor implements ServiceExecutor {
    private static final int RESTART_ATTEMPTS_LIMIT = 5;
    private static final int STATUS_ATTEMPTS_LIMIT = 5;

    public Response getServiceStatus(String serviceName) throws IOException, ServiceException {
        long start = System.currentTimeMillis();
        ServiceStatus serviceStatus = getServiceStatusEnum(serviceName);
        long end = System.currentTimeMillis();
        return new Response(String.format("Service %s status: %s", serviceName, serviceStatus), end - start);
    }

    public ServiceStatus getServiceStatusEnum(String serviceName) throws IOException, ServiceException {
        long start = System.currentTimeMillis();

        //check whether service exist
        this.checkService(serviceName);

        //check whether service is running or not
        String[] script = {"cmd", "/c", "sc", "query", serviceName, "|", "find", "/C", "\"RUNNING\""};

        Process process = Runtime.getRuntime().exec(script);
        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        //0 -> stopped; 1 -> started
        String response;
        int attempts = 0;
        while ((response = bufferedReader.readLine()) == null) {
            if (attempts >= STATUS_ATTEMPTS_LIMIT) {
                long end = System.currentTimeMillis();
                throw new ServiceException(String.format("Service status timeout: %d ms", end - start));
            }
            try {
                attempts++;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return response.equals("1") ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
    }

    public Response restartService(String serviceName) throws IOException, ServiceException {
        long start = System.currentTimeMillis();

        //check whether service exist
        this.checkService(serviceName);

        if (this.getServiceStatusEnum(serviceName) == ServiceStatus.RUNNING) {
            this.stopService(serviceName);
        }

        int attempts = 0;
        try {
            Thread.sleep(5000);
            while (this.getServiceStatusEnum(serviceName) == ServiceStatus.STOPPED) {
                if (attempts >= RESTART_ATTEMPTS_LIMIT) {
                    long end = System.currentTimeMillis();
                    throw new ServiceRestartException(String.format("Service restart failure. Time out: %d ms", end - start));
                }
                attempts++;
                this.startService(serviceName);
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            throw new ServiceStartException(String.format("Error starting %s service: %s", serviceName, e.getMessage()));
        }

        long end = System.currentTimeMillis();
        Response response = new Response((String.format("Service %s restarted successfully", serviceName)), end - start);
        return response;
    }

    public Response startService(String serviceName) throws IOException, ServiceException {
        long start = System.currentTimeMillis();

        //check whether service exist
        this.checkService(serviceName);

        Log.info(String.format("Starting %s..", serviceName));
        if (this.getServiceStatusEnum(serviceName) == ServiceStatus.RUNNING) {
            throw new ServiceStartException(String.format("The Service %s is already started.", serviceName));
        }

        // start service
        String[] script = {"cmd", "/c", "sc", "start", serviceName};

        Runtime.getRuntime().exec(script);

        long end = System.currentTimeMillis();
        Response response = new Response(String.format("Service %s started successfully.", serviceName), end - start);
        return response;
    }

    public Response stopService(String serviceName) throws IOException, ServiceException {
        long start = System.currentTimeMillis();

        //check whether service exist
        this.checkService(serviceName);

        Log.info(String.format("Stoping %s..", serviceName));
        if (this.getServiceStatusEnum(serviceName) == ServiceStatus.STOPPED) {
            throw new ServiceStopException(String.format("The Service %s is already stopped.", serviceName));
        }

        // stop service
        String[] script = {"cmd", "/c", "sc", "stop", serviceName};
        Runtime.getRuntime().exec(script);

        long end = System.currentTimeMillis();
        Response response = new Response(String.format("Service %s stoped successfully.", serviceName), end - start);
        return response;
    }

    private void checkService(String serviceName) throws IOException, ServiceException {
        if (!serviceExist(serviceName))
            throw new ServiceException(String.format("Service %s doen't exist", serviceName));
    }

    public boolean serviceExist(String serviceName) throws IOException {
        // check whether service exists
        String[] script = {"cmd", "/c", "sc", "query", serviceName};

        Process process = Runtime.getRuntime().exec(script);
        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        //not null = has response msg; 1060 - Service does not exist code
        String response = bufferedReader.readLine();

        return !response.contains("1060");
    }

}
