package org.jvmmemoryleak;

import org.springframework.boot.SpringApplication;

public class TestCase02EventListenerApplication {

	public static void main(String[] args) {
		SpringApplication.from(Case02EventListenerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
