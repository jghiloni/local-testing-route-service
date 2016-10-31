package com.ecsteam.cloudfoundry.routeservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LocalTestingRouteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalTestingRouteServiceApplication.class, args);
	}

	@Bean
	RestOperations restOperations() {
		return new RestTemplate();
	}
}
