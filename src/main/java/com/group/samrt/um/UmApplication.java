package com.group.samrt.um;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class UmApplication {

    public static void main(String[] args) {
        SpringApplication.run(UmApplication.class, args);
    }

}
