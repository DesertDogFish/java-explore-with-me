package ru.practicum.explorewithme.ewmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EwmServiceApp {
    public static void main(String[] args) {
        System.out.println("Start");
        SpringApplication.run(EwmServiceApp.class, args);
    }
}
