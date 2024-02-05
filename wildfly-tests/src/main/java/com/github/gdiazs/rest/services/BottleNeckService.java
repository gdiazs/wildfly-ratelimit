package com.github.gdiazs.rest.services;

import com.google.common.util.concurrent.RateLimiter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Named
public class BottleNeckService {

    private final static Logger LOGGER = Logger.getLogger(BottleNeckService.class.getName());

    @Resource
    private ManagedExecutorService managedExecutorService;

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    private static final RateLimiter LIMITER = RateLimiter.create(15);

    public void operation() {
        String response = this.requestUsinJaxRsClient();
    }

    public void operationAsync() {
        final int count = COUNT.incrementAndGet();
        LOGGER.info(String.format("request [%s]", count));
        this.managedExecutorService.submit(() -> {
            try {
                LIMITER.acquire(8);
                this.operation();
                LOGGER.info(String.format("service [%s] result: ok", count));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, String.format("service [%s] result: error", count));
            }

        });
    }

    private String requestUsinJaxRsClient() {
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();


        String host = System.getenv("BOTTLENECK_BASE_URL");

        String response = client.target(host)
                .path("/bottle-neck").request(MediaType.APPLICATION_JSON).get(String.class);
                
        return response;
    }

    private String requestUsingHttpClient() {

        try{
            String host = "http://localhost:9080/api";

            URL url = new URL(String.format("%s%s", host, "/bottle-neck"));
    
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            return con.getResponseMessage();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return "";
    }
}
