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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatchSDLDB {

    private static final String DELIMITER_INDICATOR = ";";
    private static final String COMMENT_INDICATOR = "//";
    private static final String SKIP_INDICATOR = "#";
    
    private static final Logger logger = LoggerFactory.getLogger(PatchSDLDB.class);
    private static final String loggerPrefix = "[Patcher] ";

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "app.datasource.className";
    static final String DB_URL = "app.datasource.url";

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
        } catch (FileNotFoundException e) {
            logger.error(loggerPrefix + INI_FILE + " file not found.");

        } catch (IOException e) {
            logger.error(loggerPrefix.concat(e.getMessage()));
        }
    }

    public boolean isSDLInstalled() {
        File f = new File(props.getProperty(INFO_FILE));
        return f.exists() && !f.isDirectory();
    }

    public void patch() {

        if (!isSDLInstalled()) {
            logger.error(loggerPrefix + "SDL not installed");
            return;
        }

        logger.info(loggerPrefix + "Patching SDLDB...");

        int patchVersion = loadLastPatchVersion();

        String[] queries = readQueryFile(props.getProperty(QUERIES_FILE), patchVersion);

        if (queries.length > 0) {
            executeQueries(queries);

        } else {
            logger.info(loggerPrefix + "SDL is up to date, no patching needed");
        }

    }

    public String[] readQueryFile(String fileUrl) {
        return readQueryFile(fileUrl, 0);
    }

    public String[] readQueryFile(String fileUrl, int skip) {
        List<String> queries = new ArrayList<>();

        logger.debug(loggerPrefix + "Reading " + fileUrl + " file...");

        Scanner scanner;
        try {
            scanner = new Scanner(new File(fileUrl));

            scanner.useDelimiter(DELIMITER_INDICATOR);

            String line = null;
            int counter = 0;

            while (scanner.hasNext()) {

                line = scanner.next().trim();

                if (counter >= skip && line.length() > 0 && !line.startsWith(COMMENT_INDICATOR)) {
                    queries.add(line);
                    
                }
                
                // Si esta buida o es un comentari no augmenta el comptador
                if (!(line.length() == 0 || line.startsWith(COMMENT_INDICATOR))) {
                    counter++;
                }

            }

        } catch (FileNotFoundException ex) {
            logger.error(loggerPrefix + "Query file not found");
            return new String[0];
        }
        
        return queries.toArray(new String[queries.size()]);
    }

    
    public void executeQueries(String[] queries) {

        openDBConnection();

        try {

            for (int i = 0; i < queries.length; i++) {
                if (!executeQuery(queries[i])) {
                    logger.error(loggerPrefix + "Query execution aborted");
                    break;
                }
            }

        } finally {

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error(loggerPrefix.concat(e.getMessage()));
            } //end finally try 
        } //end try 

        logger.info(loggerPrefix + "Patching finished.");

    }

    private void openDBConnection() {
        conn = null;

        try {
            Class.forName(props.getProperty(JDBC_DRIVER));
            logger.info(loggerPrefix + "Connecting to database...");
//            conn = DriverManager.getConnection(props.getProperty("DB_PROTOCOL") + ":"
//                    + props.getProperty("DB_URL"), props.getProperty("USER"), props.getProperty("PASS"));
            conn = DriverManager.getConnection(props.getProperty(DB_URL), props.getProperty(USER), props.getProperty(PASS));

        } catch (Exception e) {
            logger.error(loggerPrefix.concat(e.getMessage()));
        }
    }

    public boolean executeQuery(String query) {
        Statement stmt = null;
        boolean success = false;
        
        try {

            if (query.startsWith(SKIP_INDICATOR)) { // s'ha de saltar
                logger.debug(loggerPrefix + "Skiping:" + query);
                
            } else {
                logger.info(loggerPrefix + "Executing query...");
                stmt = conn.createStatement();

                stmt.executeUpdate(query);

                logger.debug(loggerPrefix + "Query executed:" + query);

                stmt.close();
            }

            lastPatchVersion++;
            saveLastPatchVersion();

            success = true;

        } catch (Exception e) { // Includes SQLException and FileNotFoundException

            logger.error(loggerPrefix.concat(e.getMessage()));

        } finally {

            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {

            } //end try 
        }

        return success;
    }

    private String readFileInfo() {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        try {
            br = new BufferedReader(new FileReader(props.getProperty(INFO_FILE)));
        } catch (FileNotFoundException e) {
            logger.error(loggerPrefix.concat(e.getMessage()));
        }

        try {

            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

        } catch (IOException e) {
            logger.error(loggerPrefix.concat(e.getMessage()));
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // No cal informar de res
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
        } else {

            lastPatchVersion = 0;
            infoFileContent = infoFileContent + "\nLastPatchVersion: \"0\"";
        }

        return lastPatchVersion;
    }

    private void saveLastPatchVersion() throws FileNotFoundException {

        int version = lastPatchVersion;
        infoFileContent = infoFileContent.replaceAll("(LastPatchVersion:\\s*\").*(\")", "$1" + version + "$2");

        logger.debug(loggerPrefix + "Updating infofile to version " + (version));

        PrintStream out = new PrintStream(new FileOutputStream(props.getProperty(INFO_FILE)));
        out.print(infoFileContent);

    }
}
