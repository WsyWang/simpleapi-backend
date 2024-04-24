package com.wsy.simpleapigateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Service;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo
@Service
public class SimpleapiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleapiGatewayApplication.class, args);
    }

}
