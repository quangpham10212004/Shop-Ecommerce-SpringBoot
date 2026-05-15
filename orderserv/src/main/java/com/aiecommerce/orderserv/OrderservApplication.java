package com.aiecommerce.orderserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderservApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderservApplication.class, args);
	}

}
