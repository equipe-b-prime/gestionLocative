package com.gestionLocative.gestionLocative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionLocativeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionLocativeApplication.class, args);
	}

}