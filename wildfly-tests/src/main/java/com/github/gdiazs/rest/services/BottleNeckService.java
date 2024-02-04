package com.github.gdiazs.rest.services;

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

    public void operation() {
        int count = COUNT.incrementAndGet();
        LOGGER.info(String.format("service [%s] request", count));
        String response = this.requestUsinJaxRsClient();
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

    private String requestUsinJaxRsClient() {
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
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
