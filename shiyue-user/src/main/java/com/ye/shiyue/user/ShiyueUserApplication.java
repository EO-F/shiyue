package com.ye.shiyue.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.ye.shiyue.user.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class ShiyueUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiyueUserApplication.class, args);
    }

}
