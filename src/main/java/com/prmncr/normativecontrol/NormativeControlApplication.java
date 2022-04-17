package com.prmncr.normativecontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NormativeControlApplication {
    public static void main(String[] args) {
        SpringApplication.run(NormativeControlApplication.class, args);
    }
}
