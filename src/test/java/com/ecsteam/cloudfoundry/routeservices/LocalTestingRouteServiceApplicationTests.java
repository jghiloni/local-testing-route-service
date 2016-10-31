package com.ecsteam.cloudfoundry.routeservices;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"wrapped.service=foo", "eureka.client.enabled=false"})
public class LocalTestingRouteServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
