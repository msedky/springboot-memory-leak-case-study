package org.jvmmemoryleak.case03;

import org.springframework.boot.SpringApplication;

public class TestCase03LargeResultsetApplication {

	public static void main(String[] args) {
		SpringApplication.from(Case03LargeResultsetApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
