package com.dipanshushukla.cop_map_central_config_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class CopMapCentralConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CopMapCentralConfigServiceApplication.class, args);
	}

}
