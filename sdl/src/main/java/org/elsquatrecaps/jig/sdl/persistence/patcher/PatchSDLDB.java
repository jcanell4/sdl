package org.elsquatrecaps.jig.sdl.persistence.patcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatchSDLDB {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "app.datasource.className";
    static final String DB_URL = "app.datasource.url";
    //static final String DB_PROTOCOL = "jdbc:h2:file";
    //  Database credentials 
    static final String USER = "app.datasource.user";
    static final String PASS = "app.datasource.pass";
    static final String INI_FILE = "./config/application.properties";
    static final String QUERIES_FILE = "app.db.patcher.queries_file";
    static final String INFO_FILE = "app.db.patcher.info_file";

    private String infoFileContent;
    private int lastPatchVersion;

    private Properties props;

    private Connection conn = null;

    public PatchSDLDB() {
        props = new Properties();

        try (FileInputStream in = new FileInputStream(INI_FILE)) {
            props.load(in);
        } catch (FileNotFoundException ex) {
            System.out.println(INI_FILE + " file not found.");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isSDLInstalled() {
        File f = new File(props.getProperty(INFO_FILE));
        return f.exists() && !f.isDirectory();
    }

    public void patch() {

        if (!isSDLInstalled()) {
            System.err.println("SDL not installed");
            return;
        }

        System.out.println("Patching SDLDB...");

        int patchVersion = loadLastPatchVersion();

        String[] queries = readQueryFile(props.getProperty(QUERIES_FILE), patchVersion);

        if (queries.length > 0) {
            executeQueries(queries);

        } else {
            System.out.println("SDL is up to date, no patching needed");
        }

    }

    public String[] readQueryFile(String fileUrl) {
        return readQueryFile(fileUrl, 0);
    }

    public String[] readQueryFile(String fileUrl, int skip) {
        List<String> queries = new ArrayList<>();

        System.out.println("Reading " + fileUrl + " file...");

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(fileUrl));
        } catch (FileNotFoundException ex) {

            System.err.println("Query file not found");
            return new String[0];
        }

        int counter = 0;

        try {
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {

                if (counter >= skip) {
                    queries.add(line);
                }

                counter++;
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        } finally {

            try {
                bufferedReader.close();
            } catch (IOException ex) {

            }

        }

        return queries.toArray(new String[queries.size()]);
    }

    public void executeQueries(String[] queries) {

        openDBConnection();

        try {
            for (int i = 0; i < queries.length; i++) {
                if (!executeQuery(queries[i])) {
                    System.err.println("Query execution aborted");
                    break;
                }
            }

        } finally {

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            } //end finally try 
        } //end try 

        System.out.println("Patching finished.");

    }

    private void openDBConnection() {
        conn = null;

        try {
            Class.forName(props.getProperty(JDBC_DRIVER));
            System.out.println("Connecting to database...");
//            conn = DriverManager.getConnection(props.getProperty("DB_PROTOCOL") + ":"
//                    + props.getProperty("DB_URL"), props.getProperty("USER"), props.getProperty("PASS"));
            conn = DriverManager.getConnection(props.getProperty(DB_URL), props.getProperty(USER), props.getProperty(PASS));

        } catch (SQLException se) {
            //Handle errors for JDBC 
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName 
            e.printStackTrace();
        }
    }

    public boolean executeQuery(String query) {
        Statement stmt = null;

        try {

            System.out.println("Executing query...");
            stmt = conn.createStatement();

            stmt.executeUpdate(query);

            System.out.println("Query executed:" + query);

            stmt.close();

            lastPatchVersion++;
            saveLastPatchVersion();

            return true;

        } catch (SQLException se) {
            //Handle errors for JDBC 
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName 
            e.printStackTrace();
        } finally {

            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {

            } //end try 
        }

        return false;
    }

    private String readFileInfo() {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        try {
            br = new BufferedReader(new FileReader(props.getProperty(INFO_FILE)));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        try {

            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ex) {

            }
        }

        return sb.toString();
    }

    private int loadLastPatchVersion() {
        infoFileContent = readFileInfo();

        String pattern = "LastPatchVersion:\\s*\"(.*)\"";
        Matcher m = Pattern.compile(pattern).matcher(infoFileContent);

        if (m.find()) {
            lastPatchVersion = Integer.parseInt(m.group(1));
            //lastPatchVersion = Integer.parseInt(m.group(1)) - 1;
            //lastPatchVersion = lastPatchVersion > 0 ? lastPatchVersion : 0;
        } else {

            lastPatchVersion = 0;
            infoFileContent = infoFileContent + "\nLastPatchVersion: \"0\"";
        }

        return lastPatchVersion;
    }

    private void saveLastPatchVersion() {
        //int version = lastPatchVersion+1;
        int version = lastPatchVersion;
        infoFileContent = infoFileContent.replaceAll("(LastPatchVersion:\\s*\").*(\")", "$1" + version + "$2");

        System.out.println("Updating infofile to version " + (version));

        try (PrintStream out = new PrintStream(new FileOutputStream(props.getProperty(INFO_FILE)))) {
            out.print(infoFileContent);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

    }
}
