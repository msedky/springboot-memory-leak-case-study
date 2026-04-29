package org.jvmmemoryleak;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Case03LargeResultsetApplicationTests {

	@Test
	void contextLoads() {
	}

}
