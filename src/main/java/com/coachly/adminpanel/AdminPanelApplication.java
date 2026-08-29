package com.coachly.adminpanel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AdminPanelApplication {

    static void main(String[] args) {
        SpringApplication.run(AdminPanelApplication.class, args);
    }
}
