package dk.kvalitetsit.hjemmebehandling.configuration;

import ca.uhn.fhir.batch2.jobs.config.Batch2JobsConfig;
import ca.uhn.fhir.jpa.api.config.JpaStorageSettings;
import ca.uhn.fhir.jpa.batch2.JpaBatch2Config;
import ca.uhn.fhir.jpa.binary.api.IBinaryStorageSvc;
import ca.uhn.fhir.jpa.binstore.DatabaseBinaryContentStorageSvcImpl;
import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import ca.uhn.fhir.jpa.model.config.SubscriptionSettings;
import ca.uhn.fhir.jpa.model.entity.StorageSettings;
import ca.uhn.fhir.jpa.subscription.channel.config.SubscriptionChannelConfig;
import ca.uhn.fhir.jpa.subscription.match.config.SubscriptionProcessorConfig;
import ca.uhn.fhir.jpa.subscription.match.config.WebsocketDispatcherConfig;
import ca.uhn.fhir.jpa.subscription.match.deliver.email.EmailSenderImpl;
import ca.uhn.fhir.jpa.subscription.match.deliver.email.IEmailSender;
import ca.uhn.fhir.jpa.subscription.submit.config.SubscriptionSubmitterConfig;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.mail.MailConfig;
import ca.uhn.fhir.rest.server.mail.MailSvc;
import dk.kvalitetsit.hjemmebehandling.service.FhirServlet;
import dk.kvalitetsit.hjemmebehandling.service.HealthServlet;
import org.hibernate.dialect.Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
@EnableTransactionManagement
@Import({
        SubscriptionSubmitterConfig.class,
        SubscriptionProcessorConfig.class,
        SubscriptionChannelConfig.class,
        WebsocketDispatcherConfig.class,
        JpaBatch2Config.class,
        Batch2JobsConfig.class
})
@ServletComponentScan(basePackageClasses = {RestfulServer.class})
public class HjemmebehandlingServiceConfiguration extends SpringBootServletInitializer {

    @Value("${base.url}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    @Bean
    public SubscriptionSettings subscriptionSettings() {
        return new SubscriptionSettings();
    }

    @Bean
    public IEmailSender emailSender() {
        return theDetails -> {};
    }

    @Bean
    public ServletRegistrationBean<FhirServlet> fhirServletRegistration(@Autowired FhirServlet fhirServlet) {
        ServletRegistrationBean<FhirServlet> srb = new ServletRegistrationBean<>();
        srb.setServlet(fhirServlet);
        srb.setUrlMappings(List.of("/fhir/*"));
        return srb;
    }

    @Bean
    public ServletRegistrationBean<HealthServlet> healthServletRegistration(@Autowired HealthServlet healthServlet) {
        ServletRegistrationBean<HealthServlet> srb = new ServletRegistrationBean<>();
        srb.setServlet(healthServlet);
        srb.setUrlMappings(List.of("/health"));
        return srb;
    }

    @Bean
    public FhirServlet fhirServlet() { return new FhirServlet();}

    @Bean
    public HealthServlet healthServlet() {
        return new HealthServlet();
    }


}
