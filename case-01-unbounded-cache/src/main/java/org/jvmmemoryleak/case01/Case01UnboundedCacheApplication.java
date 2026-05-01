package org.jvmmemoryleak.case01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Case01UnboundedCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(Case01UnboundedCacheApplication.class, args);
	}

}
