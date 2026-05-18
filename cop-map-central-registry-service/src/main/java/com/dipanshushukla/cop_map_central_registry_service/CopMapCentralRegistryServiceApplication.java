package com.dipanshushukla.cop_map_central_registry_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CopMapCentralRegistryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CopMapCentralRegistryServiceApplication.class, args);
	}

}
