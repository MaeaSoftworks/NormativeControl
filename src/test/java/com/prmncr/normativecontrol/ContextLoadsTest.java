package com.prmncr.normativecontrol;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class ContextLoadsTest {
	@Autowired
	NormativeControlApplication application;
	@Test
	void contextLoads() {
		Assert.notNull(application, "App not found!");
	}
}
