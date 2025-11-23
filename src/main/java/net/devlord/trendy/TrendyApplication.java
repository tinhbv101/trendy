package net.devlord.trendy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableJpaAuditing
@EnableScheduling
public class TrendyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TrendyApplication.class, args);
    }
}

