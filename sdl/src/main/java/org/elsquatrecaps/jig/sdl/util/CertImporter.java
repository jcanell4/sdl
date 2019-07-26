/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.util;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//VERY IMPORTANT.  SOME OF THESE EXIST IN MORE THAN ONE PACKAGE!
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 *
 * @author xavier
 */
public class CertImporter {

    public static void importCerts() {

        try {

            String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);

            File file = new File(filename);

            FileInputStream in = new FileInputStream(file);
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = "changeit";
            trustStore.load(in, password.toCharArray());

            File dir = new File("certs");

            for (final File fileEntry : dir.listFiles()) {
                if (!fileEntry.isDirectory()) {
                    System.out.println(fileEntry.getName());

                    InputStream fis = new FileInputStream(fileEntry);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    CertificateFactory cf = CertificateFactory.getInstance("X.509");

                    while (bis.available() > 0) {
                        Certificate cert = cf.generateCertificate(bis);
                        trustStore.setCertificateEntry("cert_" + bis.available(), cert);
                    }

                }
            }

            in.close();

            // ALERTA: Això no funciona perquè el fitxer del keystore es readonly per la resta d'usuaris (escriptura pel sistema)
            FileOutputStream out = new FileOutputStream(file);
            trustStore.store(out, password.toCharArray());
            out.close();

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {

            e.printStackTrace();
        }

    }

}
