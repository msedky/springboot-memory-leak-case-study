package org.jvmmemoryleak.case03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Case03LargeResultsetApplication {

	public static void main(String[] args) {
		SpringApplication.run(Case03LargeResultsetApplication.class, args);
	}

}
