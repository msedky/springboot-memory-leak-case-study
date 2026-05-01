package org.jvmmemoryleak.case01;

import org.springframework.boot.SpringApplication;

public class TestCase01UnboundedCacheApplication {

	public static void main(String[] args) {
		SpringApplication.from(Case01UnboundedCacheApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
