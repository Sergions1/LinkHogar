package com.linkhogar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LinkHogarApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkHogarApplication.class, args);
    }

}
