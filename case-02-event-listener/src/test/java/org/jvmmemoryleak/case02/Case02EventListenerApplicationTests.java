package org.jvmmemoryleak.case02;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Case02EventListenerApplicationTests {

	@Test
	void contextLoads() {
	}

}
