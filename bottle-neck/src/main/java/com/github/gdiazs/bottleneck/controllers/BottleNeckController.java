package com.github.gdiazs.bottleneck.controllers;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BottleNeckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BottleNeckController.class);

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    @Value("${bottleneck.count}")
    private Integer bottleNecCount;

    @GetMapping("/bottle-neck")
    public ResponseEntity<String> get() {

        int currentCount = COUNT.incrementAndGet();

        LOGGER.info("serving: {}", currentCount);

        if (currentCount > bottleNecCount) {
            LOGGER.error("System failed");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    "Bad Gateway 502. Service not available");
        }

        try {
            Thread.sleep(7000);
            COUNT.decrementAndGet();
        } catch (InterruptedException e) {
            LOGGER.error("Error when sleeping thread", e);
        }

        LOGGER.info("Finished! {}", currentCount);
        return ResponseEntity.ok("success!");
    }
}
