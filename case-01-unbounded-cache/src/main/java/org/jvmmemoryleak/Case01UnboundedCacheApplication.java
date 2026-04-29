package org.jvmmemoryleak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Case01UnboundedCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(Case01UnboundedCacheApplication.class, args);
	}

}
