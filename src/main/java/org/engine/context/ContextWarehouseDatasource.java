package org.engine.context;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
	    basePackages = "org.engine.warehouse.service",
	    entityManagerFactoryRef = "warehouseEntityManagerFactory",
	    transactionManagerRef = "warehouseTransactionManager"
	)
@EnableTransactionManagement
public class ContextWarehouseDatasource {

	@Autowired
    Environment environment;

	@Bean(name = "warehouseExceptionTranslation")
	@ConfigurationProperties("spring.warehouse.datasource")
	public PersistenceExceptionTranslationPostProcessor productionExceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}


	@Bean(name = "warehouseDataSourceProperties")
	@ConfigurationProperties(prefix = "spring.warehouse.datasource")
	public DataSource DS2DataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(environment.getProperty("spring.warehouse.datasource.driver-class-name"));
		dataSource.setJdbcUrl(environment.getProperty("spring.warehouse.datasource.jdbc-url"));
		dataSource.setUsername(environment.getProperty("spring.warehouse.datasource.username"));
		dataSource.setPassword(environment.getProperty("spring.warehouse.datasource.password"));
		/**
		 * HikariCP specific properties. Remove if you move to other connection pooling library.
		 **/
		dataSource.addDataSourceProperty("cachePrepStmts", true);
		dataSource.addDataSourceProperty("prepStmtCacheSize", 25000);
		dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 20048);
		dataSource.addDataSourceProperty("useServerPrepStmts", true);
		dataSource.addDataSourceProperty("initializationFailFast", true);
		dataSource.setPoolName("WAREHOUSE_HIKARICP_CONNECTION_POOL");
		return dataSource;
	}


	@Bean(name = "warehouseEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean DS2EntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(DS2DataSource());
		em.setPersistenceUnitName("DS2");
		em.setPackagesToScan(new String[] { "org.engine.warehouse.entity" });
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.create-database-schemas", true);
		properties.put(AvailableSettings.HBM2DDL_AUTO, environment.getProperty("spring.warehouse.datasource.jpa.hibernate.ddl-auto"));
		properties.put(AvailableSettings.SHOW_SQL, environment.getProperty("spring.warehouse.datasource.jpa.show-sql"));
		em.setJpaPropertyMap(properties);
		return em;
	}


	@Bean(name = "warehouseTransactionManager")
	public PlatformTransactionManager DS2TransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(DS2EntityManagerFactory().getObject());
		return transactionManager;
	}
}
