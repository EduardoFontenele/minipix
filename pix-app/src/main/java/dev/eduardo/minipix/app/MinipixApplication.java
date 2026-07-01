package dev.eduardo.minipix.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "dev.eduardo.minipix")
@EntityScan("dev.eduardo.minipix")
@EnableJpaRepositories("dev.eduardo.minipix")
public class MinipixApplication {

    static void main(String[] args) {
        SpringApplication.run(MinipixApplication.class, args);
    }
}
