package net.devlord.trendy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TrendyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TrendyApplication.class, args);
    }
}

