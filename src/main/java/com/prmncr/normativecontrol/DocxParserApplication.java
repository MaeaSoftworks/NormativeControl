package com.prmncr.normativecontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DocxParserApplication {
	public static void main(String[] args) {
		SpringApplication.run(DocxParserApplication.class, args);
	}
}
