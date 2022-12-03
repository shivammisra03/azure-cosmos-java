package com.shivam.cosmos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CosmosSqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosmosSqlApplication.class, args);
	}

	@Bean
	ObjectMapper objectMapper(){
		return new ObjectMapper();
	}

}
