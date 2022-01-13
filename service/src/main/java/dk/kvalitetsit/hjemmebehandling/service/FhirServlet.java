package dk.kvalitetsit.hjemmebehandling.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.provider.IJpaSystemProvider;
import ca.uhn.fhir.jpa.rp.r4.CarePlanResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.PatientResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.OrganizationResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.PlanDefinitionResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.PractitionerResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.QuestionnaireResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.QuestionnaireResponseResourceProvider;
import ca.uhn.fhir.jpa.rp.r4.SearchParameterResourceProvider;

import ca.uhn.fhir.rest.api.EncodingEnum;
//import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;
//import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import dk.kvalitetsit.hjemmebehandling.configuration.HjemmebehandlingServiceConfiguration;
import dk.kvalitetsit.hjemmebehandling.interceptor.MethodTimerInterceptor;
//import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
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
  private PractitionerResourceProvider practitionerResourceProvider;

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

  @Autowired
  IJpaSystemProvider jpaSystemProvider;

  @Override
  protected void initialize() throws ServletException {
    super.initialize();
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());

    FhirContext ctx = FhirContext.forR4();
    setFhirContext(ctx);

    setResourceProviders(Arrays.asList(carePlanResourceProvider, patientResourceProvider, practitionerResourceProvider, organizationResourceProvider, planDefinitionResourceProvider, questionnaireResourceProvider, questionnaireResponseResourceProvider, searchParameterResourceProvider));
    registerProvider(jpaSystemProvider); // handles system level transaction. (Bundle that consists of a number of resources to be created in one transaction)

    // Create an interceptor to validate incoming requests
//    RequestValidatingInterceptor requestInterceptor = new RequestValidatingInterceptor();

    // Register a validator module (you could also use SchemaBaseValidator and/or SchematronBaseValidator)
//    requestInterceptor.addValidatorModule(new FhirInstanceValidator(ctx));

//    requestInterceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
//    requestInterceptor.setAddResponseHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
//    requestInterceptor.setResponseHeaderValue("Validation on ${line}: ${message} ${severity}");
//    requestInterceptor.setResponseHeaderValueNoIssues("No issues detected");

    // Now register the validating interceptor
//    registerInterceptor(requestInterceptor);

    setDefaultPrettyPrint(true);
    setDefaultResponseEncoding(EncodingEnum.JSON);

    registerInterceptor(methodInterceptor);

//    OpenApiInterceptor openApiInterceptor = new OpenApiInterceptor();
//    registerInterceptor(openApiInterceptor);

    String serverBaseUrl = configurationValues.getBaseUrl();
    setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBaseUrl));
  }
}
