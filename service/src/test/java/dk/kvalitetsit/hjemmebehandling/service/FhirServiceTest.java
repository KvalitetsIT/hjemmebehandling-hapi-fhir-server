package dk.kvalitetsit.hjemmebehandling.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hamcrest.Matchers;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.micrometer.core.instrument.util.StringUtils.isNotBlank;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FhirServiceTest {

    private static FhirServlet helloService;

    @BeforeAll
    public static void setup() {
        helloService = new FhirServlet();
    }

//    @Test
//    public void testValidInput() {
//        var input = new HelloServiceInput();
//        input.setName(UUID.randomUUID().toString());
//
//        var result = helloService.helloServiceBusinessLogic(input);
//        assertNotNull(result);
//        assertNotNull(result.getNow());
//        assertEquals(input.getName(), result.getName());
//    }

    @Test
    public void testme() {
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/fhir");
        try {
            Bundle results = client
                .search()
                .forResource("nogetsludder")
                //.forResource(Patient.class)
                //.where(Patient.FAMILY.matches().value("duck"))
                .returnBundle(Bundle.class)
                .execute();
            System.out.println("Found " + results.getEntry().size() + " patients named 'duck'");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void conformanceCheck() {
//        FhirContext ctx = FhirContext.forR4();
//        IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/fhir");
//
//        CapabilityStatement cs = client.capabilities().ofType(CapabilityStatement.class).execute();
//        CapabilityStatement.CapabilityStatementRestResourceComponent resource = cs.getRest().get(0).getResource().get(0);
//        List<String> definitions = resource.getSearchParam()
//            .stream()
//            .filter(t -> isNotBlank(t.getDefinition()))
//            .map(t->t.getDefinition())
//            .sorted()
//            .collect(Collectors.toList());
//
//        List<String> supportedResources = cs.getRest().get(0).getResource().stream()
//            .map(t -> t.getProfile())
//            .collect(Collectors.toList());
//
//        assertThat(supportedResources.toString(), supportedResources, Matchers.contains(
//            "http://hl7.org/fhir/StructureDefinition/CarePlan",
//            "http://hl7.org/fhir/StructureDefinition/OperationDefinition",
//            "http://hl7.org/fhir/StructureDefinition/Organization",
//            "http://hl7.org/fhir/StructureDefinition/Patient",
//            "http://hl7.org/fhir/StructureDefinition/PlanDefinition",
//            "http://hl7.org/fhir/StructureDefinition/Questionnaire",
//            "http://hl7.org/fhir/StructureDefinition/QuestionnaireResponse",
//            "http://hl7.org/fhir/StructureDefinition/SearchParameter"
//        ));
//    }


}
