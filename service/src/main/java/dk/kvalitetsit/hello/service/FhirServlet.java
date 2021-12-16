package dk.kvalitetsit.hello.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.rp.r4.CarePlanResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.PatientResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.OrganizationResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.PlanDefinitionResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.QuestionnaireResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.QuestionnaireResponseResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.SearchParameterResourceProvider;

import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import dk.kvalitetsit.hello.configuration.HjemmebehandlingServiceConfiguration;
import dk.kvalitetsit.hello.interceptor.MethodTimerInterceptor;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import java.util.Arrays;

public class FhirServlet extends RestfulServer {
  private static final long serialVersionUID = 1L;

  @Autowired
  private HjemmebehandlingServiceConfiguration configurationValues;

  @Autowired
  private CarePlanResourceProvider carePlanResourceProvider;

  @Autowired
  private PatientResourceProvider patientResourceProvider;

  @Autowired
  private OrganizationResourceProvider organizationResourceProvider;

  @Autowired
  private PlanDefinitionResourceProvider planDefinitionResourceProvider;

  @Autowired
  private QuestionnaireResourceProvider questionnaireResourceProvider;

  @Autowired
  private QuestionnaireResponseResourceProvider questionnaireResponseResourceProvider;

  @Autowired
  private  SearchParameterResourceProvider searchParameterResourceProvider;

  @Autowired
  private MethodTimerInterceptor methodInterceptor;

  @Override
  protected void initialize() throws ServletException {
    super.initialize();
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());

    FhirContext ctx = FhirContext.forR4();
    setFhirContext(ctx);

    setResourceProviders(Arrays.asList(carePlanResourceProvider, patientResourceProvider, organizationResourceProvider, planDefinitionResourceProvider, questionnaireResourceProvider, questionnaireResponseResourceProvider, searchParameterResourceProvider));

    // Create an interceptor to validate incoming requests
    RequestValidatingInterceptor requestInterceptor = new RequestValidatingInterceptor();

    // Register a validator module (you could also use SchemaBaseValidator and/or SchematronBaseValidator)
    requestInterceptor.addValidatorModule(new FhirInstanceValidator(ctx));

    requestInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
    requestInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
    requestInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
    requestInterceptor.setResponseHeaderValueNoIssues("No issues detected");

    // Now register the validating interceptor
//    registerInterceptor(requestInterceptor);

    setDefaultPrettyPrint(true);
    setDefaultResponseEncoding(EncodingEnum.JSON);

    registerInterceptor(methodInterceptor);

    String serverBaseUrl = configurationValues.getBaseUrl();
    setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBaseUrl));
  }
}
