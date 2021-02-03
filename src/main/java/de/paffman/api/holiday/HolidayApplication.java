package de.paffman.api.holiday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class HolidayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayApplication.class, args);
    }
}
