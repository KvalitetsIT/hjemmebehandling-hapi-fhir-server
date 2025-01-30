package dk.kvalitetsit.hjemmebehandling.configuration;

import dk.kvalitetsit.hjemmebehandling.service.FhirServlet;
import dk.kvalitetsit.hjemmebehandling.service.HealthServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class HjemmebehandlingServiceConfiguration {

  @Value("${base.url}")
  private String baseUrl;

  public String getBaseUrl() {
    return baseUrl;
  }

  @Autowired
  AutowireCapableBeanFactory beanFactory;

  @Bean
  public ServletRegistrationBean<FhirServlet> fhirServletRegistration(@Autowired FhirServlet fhirServlet) {
    ServletRegistrationBean<FhirServlet> srb = new ServletRegistrationBean<FhirServlet>();
    beanFactory.autowireBean(fhirServlet);
    srb.setServlet(fhirServlet);
    srb.setUrlMappings(Arrays.asList("/fhir/*"));
    return srb;
  }

  @Bean
  public ServletRegistrationBean<HealthServlet> healthServletRegistration(@Autowired HealthServlet healthServlet) {
    ServletRegistrationBean<HealthServlet> srb = new ServletRegistrationBean<HealthServlet>();
    beanFactory.autowireBean(healthServlet);
    srb.setServlet(healthServlet);
    srb.setUrlMappings(Arrays.asList("/health"));
    return srb;
  }

  @Bean
  public FhirServlet fhirServlet() {
      return new FhirServlet();
  }

  @Bean
  public HealthServlet healthServlet() {
    return new HealthServlet();
  }
}
