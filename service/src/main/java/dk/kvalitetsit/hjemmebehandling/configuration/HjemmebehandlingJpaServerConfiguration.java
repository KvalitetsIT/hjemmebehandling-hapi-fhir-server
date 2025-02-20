package dk.kvalitetsit.hjemmebehandling.configuration;

import ca.uhn.fhir.context.ConfigurationException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.IDaoRegistry;
import ca.uhn.fhir.jpa.api.config.JpaStorageSettings;
import ca.uhn.fhir.jpa.api.config.ThreadPoolFactoryConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.config.r4.JpaR4Config;
import ca.uhn.fhir.jpa.config.util.HapiEntityManagerFactoryUtil;
import ca.uhn.fhir.jpa.config.util.ResourceCountCacheUtil;
import ca.uhn.fhir.jpa.config.util.ValidationSupportConfigUtil;
import ca.uhn.fhir.jpa.dao.search.HSearchSortHelperImpl;
import ca.uhn.fhir.jpa.dao.search.IHSearchSortHelper;
import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import ca.uhn.fhir.jpa.provider.DaoRegistryResourceSupportedSvc;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.util.ResourceCountCache;
import ca.uhn.fhir.jpa.validation.JpaValidationSupportChain;
import ca.uhn.fhir.rest.api.IResourceSupportedSvc;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;
import dk.kvalitetsit.hjemmebehandling.interceptor.MethodTimerInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import org.hibernate.search.mapper.orm.cfg.HibernateOrmMapperSettings;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;


import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Import({
        ThreadPoolFactoryConfig.class,
        JpaR4Config.class
})
public class HjemmebehandlingJpaServerConfiguration {

    @Autowired
    private DataSource dataSource;

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource myDataSource,
            ConfigurableListableBeanFactory myConfigurableListableBeanFactory,
            FhirContext theFhirContext,
            JpaStorageSettings theStorageSettings
    ) {
        LocalContainerEntityManagerFactoryBean retVal = HapiEntityManagerFactoryUtil.newEntityManagerFactory(myConfigurableListableBeanFactory, theFhirContext, theStorageSettings);
        retVal.setPersistenceUnitName("HAPI_PU");

        try {
            retVal.setDataSource(myDataSource);
        } catch (Exception e) {
            throw new ConfigurationException("Could not set the data source due to a configuration issue", e);
        }
        retVal.setJpaProperties(jpaProperties());
        return retVal;
    }

    private Properties jpaProperties() {
        Properties extraProperties = new Properties();

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

    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public JpaStorageSettings jpaStorageSettings() {
        JpaStorageSettings retVal = new JpaStorageSettings();
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
        return new PartitionSettings();
    }

    @Bean
    public MethodTimerInterceptor methodInterceptor(MeterRegistry meterRegistry) {
        return new MethodTimerInterceptor(meterRegistry);
    }

    @Primary
    @Bean
    public CachingValidationSupport validationSupportChain(JpaValidationSupportChain theJpaValidationSupportChain) {
        return ValidationSupportConfigUtil.newCachingValidationSupport(theJpaValidationSupportChain);
    }

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    @Bean
    public DatabaseBackedPagingProvider databaseBackedPagingProvider() {
        return new DatabaseBackedPagingProvider();
    }

    @Bean
    public IResourceSupportedSvc resourceSupportedSvc(IDaoRegistry theDaoRegistry) {
        return new DaoRegistryResourceSupportedSvc(theDaoRegistry);
    }
}
