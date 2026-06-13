package org.example;

import org.example.model.Node;
import org.example.model.NodeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.example.model")
@EnableJpaRepositories("org.example.model")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner init(NodeRepository repo) {
        return args -> {

            System.out.println(">>> SERVER PORNIT! ADAUGĂ PUNCTE DIN BROWSER.");
        };
    }
}