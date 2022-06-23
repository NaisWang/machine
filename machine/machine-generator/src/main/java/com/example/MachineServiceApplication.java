package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : whz
 */
@SpringBootApplication
@MapperScan("com.nais.sz.*.mapper")
public class MachineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MachineServiceApplication.class, args);
	}

}
