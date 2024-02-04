package com.github.gdiazs.rest.services;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Named
public class BottleNeckService {

    private final static Logger LOGGER = Logger.getLogger(BottleNeckService.class.getName());

    @Resource
    private ManagedExecutorService managedExecutorService;

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    public void operation() {
        int count = COUNT.incrementAndGet();
        LOGGER.info(String.format("service [%s] request", count));
        String response = requestBottleNeckEndpoint();
        LOGGER.info(String.format("service [%s] result: %s", count, response));
    }

    public void operationAsync() {
        this.managedExecutorService.submit(() -> {
            try {
                this.operation();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "async failed", e);
            }

        });
    }

    private String requestBottleNeckEndpoint() {
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .executorService(this.managedExecutorService)
                .build();


        String host = System.getenv("BOTTLENECK_BASE_URL");

        String response = client.target(host)
                .path("/bottle-neck").request(MediaType.APPLICATION_JSON).get(String.class);
                
        return response;
    }
}
