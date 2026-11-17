package com.tarek.OAuth2_Internal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OAuth2InternalApplication {


	public static void main(String[] args) {
		SpringApplication.run(OAuth2InternalApplication.class, args);
	}

}