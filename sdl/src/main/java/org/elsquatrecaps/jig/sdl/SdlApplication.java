package org.elsquatrecaps.jig.sdl;

import org.hsqldb.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class SdlApplication {
//        @Autowired
//        Server hsqldbServer;

	public static void main(String[] args) {
		SpringApplication.run(SdlApplication.class, args);
	}
}
