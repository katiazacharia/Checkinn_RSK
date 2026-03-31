package com.project.checkinn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class CheckinnApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckinnApplication.class, args);
	}

}
