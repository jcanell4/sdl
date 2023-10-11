package org.elsquatrecaps.jig.sdl;

import org.elsquatrecaps.jig.sdl.persistence.patcher.PatchSDLDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication
public class SdlApplication {

	public static void main(String[] args) {
            
            System.setProperty("javax.net.ssl.trustStore", "certs/cacerts");
            patchDb();
            SpringApplication.run(SdlApplication.class, args);
	}
        
        private static void patchDb(){
            PatchSDLDB updater = new PatchSDLDB();
            updater.patch();            
        }
}
