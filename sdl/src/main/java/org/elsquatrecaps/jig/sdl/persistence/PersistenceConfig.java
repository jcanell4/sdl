
package org.elsquatrecaps.jig.sdl.persistence;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hsqldb.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaRepositories(
  entityManagerFactoryRef = "entityManagerFactory",
  basePackages = {"org.elsquatrecaps.jig.sdl.persistence"},
  transactionManagerRef = "transactionManager"
)
@EnableTransactionManagement
@EnableConfigurationProperties
public class PersistenceConfig {
    @Autowired
    Server hsqlServer;
//    private final Logger log = LoggerFactory.getLogger(getClass());
            
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConfigurationProperties//(prefix = "alarms.idempotent.server")
    public Server hsqldbServer() {

        Server server = new Server();
        server.setDatabaseName(0, "sdldb");
        server.setDatabasePath(0, "./db/sdldb");
        server.setPort(9090);
//        server.setLogWriter(slf4jPrintWriter());
//        server.setErrWriter(slf4jPrintWriter());

        return server;

    }  

    @Bean
    @ConfigurationProperties(prefix="spring.jpa")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(hsqldbDataSource());
        emf.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPackagesToScan("org.elsquatrecaps.jig.sdl.model");
        emf.setJpaPropertyMap(getHsqldbJpaProperties());
        emf.setPersistenceUnitName("org.elsquatrecaps.jig_sdl_jar_0.0.1-SNAPSHOTPU");
          
        return emf;
    }
    
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject() );
        return transactionManager;         
     }
    
    @Bean
    public DataSource hsqldbDataSource() {
        String dname = org.hsqldb.jdbcDriver.class.getName();
        return DataSourceBuilder
                .create().url("jdbc:hsqldb:hsql://localhost:9090/sdldb")
                .username("sa").password("")
                .driverClassName(dname)
                .build();
    }
    
    @Bean
    public DataSource derbyDataSource() {
        String dname = org.apache.derby.jdbc.ClientDriver.class.getName();
        return DataSourceBuilder
                .create().url("jdbc:derby://localhost:1527/sdldb;create=true;user=app;password=app")
                .username("app").password("app")
                .driverClassName(dname)
                .build();
    }
    
    public Map<String, String> getDerbyJpaProperties() {
        HashMap<String, String> map = new HashMap<>();
        map.put("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");
        map.put("hibernate.ddl-auto", "update");
        map.put("spring.jpa.show-sql", "true");
        map.put("javax.persistence.schema-generation.database.action", "create");
        return map;
    }
    
    public Map<String, String> getHsqldbJpaProperties() {
        HashMap<String, String> map = new HashMap<>();
        map.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        map.put("hibernate.ddl-auto", "update");
        map.put("spring.jpa.show-sql", "true");
        map.put("javax.persistence.schema-generation.database.action", "create");
        return map;
    }
    
    
//    private PrintWriter slf4jPrintWriter() {
//        PrintWriter printWriter = new PrintWriter(new ByteArrayOutputStream()) {
//            @Override
//            public void println(final String x) {
//                log.debug(x);
//            }
//        };
//        return printWriter;
//    }
    
//    @Bean("idempotentDataSource")
//    @Primary
//    @ConfigurationProperties
//    public DataSource idempotentDataSource(@Value("${alarms.idempotent.datasource.url}") String urlNoPath, @Value("${alarms.idempotent.name}") String name) {
//        JDBCDataSource jdbcDataSource = new JDBCDataSource();
//        String url = urlNoPath;
//        if (!url.endsWith("/")) {
//            url += "/";
//        }
//        url += name;
//        jdbcDataSource.setUrl(url);
//        jdbcDataSource.setUser("sa");
//        return jdbcDataSource;
//    }
}