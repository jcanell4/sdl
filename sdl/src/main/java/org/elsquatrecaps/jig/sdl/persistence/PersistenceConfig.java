
package org.elsquatrecaps.jig.sdl.persistence;

import javax.sql.DataSource;
import org.elsquatrecaps.jig.sdl.configuration.DataSourceProperties;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.configuration.InfoInstallBean;
import org.hibernate.jpa.HibernatePersistenceProvider;
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
    DownloaderProperties dp;
    @Autowired
    DataSourceProperties dsp;
    @Autowired
    InfoInstallBean infoInstallBean;
    
//    private final Logger log = LoggerFactory.getLogger(getClass());
            
    @Bean(destroyMethod = "close")
    public InfoInstallBean getPropertiesBean() {
        InfoInstallBean prop = new InfoInstallBean();
        return prop;
    }  

    @Bean
    @ConfigurationProperties(prefix="spring.jpa")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(getDataSource());
        emf.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPackagesToScan("org.elsquatrecaps.jig.sdl.model");
        if(!infoInstallBean.isDataBaseInstalled()){
            dsp.getProperties().put("javax.persistence.schema-generation.database.action", "create");
        }
        emf.setJpaPropertyMap(dsp.getProperties());
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
    public DataSource getDataSource() {
        String username = dsp.getName().equalsIgnoreCase("derby")?"app":"sa";
        String password = dsp.getName().equalsIgnoreCase("derby")?"app":"sa";
        String dname = dsp.getClassName();
        return DataSourceBuilder
                .create().url(dsp.getUrl())
                .username(username).password(password)
                .driverClassName(dname)
                .build();
    }
    
//    public Map<String, String> getDerbyJpaProperties() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");
//        map.put("hibernate.ddl-auto", "update");
//        map.put("spring.jpa.show-sql", "true");
//        map.put("javax.persistence.schema-generation.database.action", "create");
//        return map;
//    }
//    
//    public Map<String, String> getHsqldbJpaProperties() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
//        map.put("hibernate.ddl-auto", "update");
//        map.put("spring.jpa.show-sql", "true");
//        map.put("javax.persistence.schema-generation.database.action", "create");
//        return map;
//    }
    
    
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