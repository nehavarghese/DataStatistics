package com.demo.helloFresh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "com.demo.helloFresh.*")
@EnableSwagger2
@EnableScheduling
public class HelloFreshApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloFreshApplication.class, args);
	}

}
