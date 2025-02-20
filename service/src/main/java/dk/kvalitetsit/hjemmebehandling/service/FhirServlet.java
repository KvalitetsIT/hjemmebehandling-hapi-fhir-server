package dk.kvalitetsit.hjemmebehandling.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.provider.IJpaSystemProvider;
import ca.uhn.fhir.jpa.rp.r4.*;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;
import dk.kvalitetsit.hjemmebehandling.configuration.HjemmebehandlingServiceConfiguration;
import dk.kvalitetsit.hjemmebehandling.interceptor.MethodTimerInterceptor;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.List;

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
    private SearchParameterResourceProvider searchParameterResourceProvider;

    @Autowired
    private CodeSystemResourceProvider codeSystemResourceProvider;

    @Autowired
    private ValueSetResourceProvider valueSetResourceProvider;

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

        setResourceProviders(List.of(
                carePlanResourceProvider,
                patientResourceProvider,
                practitionerResourceProvider,
                organizationResourceProvider,
                planDefinitionResourceProvider,
                questionnaireResourceProvider,
                questionnaireResponseResourceProvider,
                searchParameterResourceProvider,
                codeSystemResourceProvider,
                valueSetResourceProvider));

        registerProvider(jpaSystemProvider); // handles system level transaction. (Bundle that consists of a number of resources to be created in one transaction)
        registerInterceptor(methodInterceptor);

        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

        String serverBaseUrl = configurationValues.getBaseUrl();
        setServerAddressStrategy(new HardcodedServerAddressStrategy(serverBaseUrl));
    }
}
