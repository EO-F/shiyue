package com.ye.shiyue.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ShiyueSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiyueSearchApplication.class, args);
    }

}
