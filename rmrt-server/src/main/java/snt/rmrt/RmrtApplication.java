package snt.rmrt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RmrtApplication {

    public static void main(String[] args) {
        SpringApplication.run(RmrtApplication.class, args);
    }

}
