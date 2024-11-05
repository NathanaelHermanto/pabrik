package com.nathancorp.pabrik;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.config.location=classpath:/test.properties")
class PabrikApplicationTests {

	@Test
	void contextLoads() {
	}

}
