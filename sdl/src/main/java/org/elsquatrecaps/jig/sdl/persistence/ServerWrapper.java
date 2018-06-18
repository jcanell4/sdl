/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.persistence;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Properties;
import org.apache.derby.drda.NetworkServerControl;
import org.elsquatrecaps.jig.sdl.configuration.ServerProperties;
import org.hsqldb.Server;

/**
 *
 * @author josep
 */
public class ServerWrapper {
    ServerProperties properties;
    String serverName=null;
    Server server=null;
    NetworkServerControl networkServerControl;
    PrintWriter pw=null;

    public ServerWrapper(ServerProperties prop) {
        properties = prop;
        serverName = prop.getName();
        switch(serverName){
            case "derby":
                try {
                    Properties p;
                    p = System.getProperties();
                    p.setProperty("derby.system.home", prop.getFile());
                    networkServerControl = new NetworkServerControl(InetAddress.getByName(prop.getUrl()), prop.getServerPort(), "app", "app");
                }catch(Exception e){
                    
                }
                break;
            case "hsqldb":
                try {
                    server = new Server();
                    server.setDaemon(true);
                    server.setDatabaseName(0, prop.getFile());
                    server.setAddress(prop.getUrl());
                    server.setPort(prop.getServerPort());
                    File fWriter = new File(properties.getLogfilename());
                    fWriter.getParentFile().mkdirs();
                    pw = new PrintWriter(prop.getLogfilename());
                    server.setLogWriter(pw);
                    server.setErrWriter(pw);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
    
    public void start(){
        if(networkServerControl!=null){
            try {
                File fWriter = new File(properties.getLogfilename());
                fWriter.getParentFile().mkdirs();
                pw = new PrintWriter(properties.getLogfilename());
                networkServerControl.start(pw);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(server!=null){
            server.start();
        }
    }
    
    public void stop(){
        if(networkServerControl!=null){
            try {
                networkServerControl.shutdown();
            }catch(Exception e){

            }
        }else if(server!=null){
            server.stop();
        }
        if(pw!=null){
            pw.close();
        }
    }
}
