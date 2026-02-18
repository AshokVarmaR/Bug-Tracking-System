package com.bts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BugTrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugTrackingSystemApplication.class, args);
	}

}
