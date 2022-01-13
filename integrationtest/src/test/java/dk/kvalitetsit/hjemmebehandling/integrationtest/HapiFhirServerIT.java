package dk.kvalitetsit.hjemmebehandling.integrationtest;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hamcrest.Matchers;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.junit.Test;
import org.openapitools.client.ApiException;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class HapiFhirServerIT extends AbstractIntegrationTest {

    private final IGenericClient client;

    public HapiFhirServerIT() {
        FhirContext ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient(getApiBasePath() + "/fhir");
    }

    @Test
    public void testHapiServerConformance() throws ApiException {
        CapabilityStatement cs = client.capabilities().ofType(CapabilityStatement.class).execute();
        CapabilityStatement.CapabilityStatementRestResourceComponent resource = cs.getRest().get(0).getResource().get(0);

        List<String> supportedResources = cs.getRest().get(0).getResource().stream()
            .map(t -> t.getProfile())
            .collect(Collectors.toList());

        assertEquals(9, supportedResources.size());

        assertThat(supportedResources.toString(), supportedResources, Matchers.contains(
            "http://hl7.org/fhir/StructureDefinition/CarePlan",
            "http://hl7.org/fhir/StructureDefinition/OperationDefinition",
            "http://hl7.org/fhir/StructureDefinition/Organization",
            "http://hl7.org/fhir/StructureDefinition/Patient",
            "http://hl7.org/fhir/StructureDefinition/PlanDefinition",
            "http://hl7.org/fhir/StructureDefinition/Practitioner",
            "http://hl7.org/fhir/StructureDefinition/Questionnaire",
            "http://hl7.org/fhir/StructureDefinition/QuestionnaireResponse",
            "http://hl7.org/fhir/StructureDefinition/SearchParameter"
        ));
    }
}
