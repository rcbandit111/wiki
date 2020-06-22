package org.engine.context;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
	    basePackages = "org.engine.production.service",
	    entityManagerFactoryRef = "productionEntityManagerFactory", 
	    transactionManagerRef = "productionTransactionManager"
	)
@EnableTransactionManagement
public class ContextProductionDatasource {

	@Autowired
    Environment environment;

	@Primary
	@Bean(name = "productionExceptionTranslation")
	@ConfigurationProperties("spring.production.datasource")
    public PersistenceExceptionTranslationPostProcessor productionExceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

	@Primary
	@Bean(name = "productionDataSourceProperties")
	@ConfigurationProperties(prefix = "spring.production.datasource")
	public DataSource DS1DataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(environment.getProperty("spring.production.datasource.driver-class-name"));
		dataSource.setJdbcUrl(environment.getProperty("spring.production.datasource.jdbc-url"));
		dataSource.setUsername(environment.getProperty("spring.production.datasource.username"));
		dataSource.setPassword(environment.getProperty("spring.production.datasource.password"));


		/**
		 * HikariCP specific properties. Remove if you move to other connection pooling library.
		 **/
		dataSource.addDataSourceProperty("cachePrepStmts", true);
		dataSource.addDataSourceProperty("prepStmtCacheSize", 25000);
		dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 20048);
		dataSource.addDataSourceProperty("useServerPrepStmts", true);
		dataSource.addDataSourceProperty("initializationFailFast", true);
		dataSource.setPoolName("PRODUCTION_HIKARICP_CONNECTION_POOL");
		return dataSource;
	}

	@Primary
	@Bean(name = "productionEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean DS1EntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(DS1DataSource());
		em.setPersistenceUnitName("DS1");
		em.setPackagesToScan(new String[] { "org.engine.production.entity" });
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.create-database-schemas", true);
		properties.put(AvailableSettings.HBM2DDL_AUTO, environment.getProperty("spring.production.datasource.jpa.hibernate.ddl-auto"));
		properties.put(AvailableSettings.SHOW_SQL, environment.getProperty("spring.production.datasource.jpa.show-sql"));
		em.setJpaPropertyMap(properties);
		return em;
	}

	@Primary
	@Bean(name = "productionTransactionManager")
	public PlatformTransactionManager DS1TransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(DS1EntityManagerFactory().getObject());
		return transactionManager;
	}
}
