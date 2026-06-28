package dev.eduardo.minipix.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dev.eduardo.minipix")
public class MinipixApplication {

    static void main(String[] args) {
        SpringApplication.run(MinipixApplication.class, args);
    }
}
