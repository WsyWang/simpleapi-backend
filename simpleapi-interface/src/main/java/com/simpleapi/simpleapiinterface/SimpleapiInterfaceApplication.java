package com.simpleapi.simpleapiinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SimpleapiInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleapiInterfaceApplication.class, args);
    }

}
