package org.elsquatrecaps.jig.sdl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@EnableTransactionManagement
@SpringBootApplication
public class SdlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdlApplication.class, args);
	}
}
