package dk.kvalitetsit.hjemmebehandling.configuration;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.config.BaseJavaConfigR4;
import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import ca.uhn.fhir.jpa.model.entity.ModelConfig;
import dk.kvalitetsit.hjemmebehandling.interceptor.MethodTimerInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.hibernate.search.mapper.orm.cfg.HibernateOrmMapperSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement()
public class HjemmebehandlingJpaServerConfiguration extends BaseJavaConfigR4 {
//  @Override
//  protected boolean isSupported(String theResourceType) {
//    if (theResourceType.equals("AllergyIntolerance") || theResourceType.equals("Patient")) {
//      return true;
//    }
//
//    return false;
//  }

  @Bean
  public ModelConfig modelConfig() {
    return daoConfig().getModelConfig();
  }

  @Autowired
  private DataSource dataSource;

  @Override
  @Bean()
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    ConfigurableListableBeanFactory myConfigurableListableBeanFactory) {
    LocalContainerEntityManagerFactoryBean retVal = super.entityManagerFactory(myConfigurableListableBeanFactory);
    retVal.setPersistenceUnitName("HAPI_PU");

    try {
      retVal.setDataSource(dataSource);
    } catch (Exception e) {
      throw new ConfigurationException("Could not set the data source due to a configuration issue", e);
    }

    retVal.setJpaProperties( jpaProperties() );
    return retVal;
  }

  private Properties jpaProperties() {
    Properties extraProperties = new Properties();
    //extraProperties.put("spring.datasource.url", "jdbc:h2:file:./target/database/h2");
    //extraProperties.put("url", "jdbc:h2:file:./target/database/h2");

    extraProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    extraProperties.put("hibernate.format_sql", "true");
    extraProperties.put("hibernate.hbm2ddl.auto", "update");
    extraProperties.put("hibernate.jdbc.batch_size", "20");
    extraProperties.put("hibernate.cache.use_query_cache", "false");
    extraProperties.put("hibernate.cache.use_second_level_cache", "false");
    extraProperties.put("hibernate.cache.use_structured_entries", "false");
    extraProperties.put("hibernate.cache.use_minimal_puts", "false");

    extraProperties.put("hibernate.generate_statistics", "true");
    extraProperties.put(HibernateOrmMapperSettings.ENABLED, "false");
    //extraProperties.put(spring.datasource.driverClassName=com.mysql.jdbc.Driver
    extraProperties.put("spring.batch.job.enabled", "false");
    extraProperties.put("elasticsearch.enabled", "false");
    return extraProperties;
  }

  @Bean()
  @Primary
  public JpaTransactionManager hapiTransactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager retVal = new JpaTransactionManager();
    retVal.setEntityManagerFactory(entityManagerFactory);
    return retVal;
  }


  @Bean
  public DaoConfig daoConfig() {
    DaoConfig retVal = new DaoConfig();
    retVal.setAllowMultipleDelete(true);
    // Added to allow services to reference objects running
    // on remote URL's
    retVal.setAllowExternalReferences(true);
    retVal.setAllowInlineMatchUrlReferences(true);
    retVal.setReuseCachedSearchResultsForMillis(null);
    retVal.setExpungeEnabled(true);

    return retVal;
  }

  @Bean
  public PartitionSettings partitionSettings() {
    PartitionSettings retVal = new PartitionSettings();

    return retVal;
  }

  @Bean
  public MethodTimerInterceptor methodInterceptor(MeterRegistry meterRegistry) {
    return new MethodTimerInterceptor(meterRegistry);
  }

  @Bean
  public MeterRegistry meterRegistry() {
    return new SimpleMeterRegistry(); // Provides a default no-op registry
  }

}
