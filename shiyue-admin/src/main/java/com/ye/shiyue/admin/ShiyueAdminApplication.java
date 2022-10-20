package com.ye.shiyue.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.ye.shiyue.admin.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class ShiyueAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiyueAdminApplication.class, args);
    }

}
