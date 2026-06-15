package com.example.prjwebservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class PrjWebServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrjWebServiceApplication.class, args);
    }

}
