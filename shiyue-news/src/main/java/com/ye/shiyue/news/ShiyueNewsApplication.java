package com.ye.shiyue.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.ye.shiyue.news.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class ShiyueNewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiyueNewsApplication.class, args);
    }

}
