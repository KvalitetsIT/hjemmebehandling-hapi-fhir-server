package dk.kvalitetsit.hello.interceptor;

import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.ResponseDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class MethodTimerInterceptor extends InterceptorAdapter {
  private static final Logger logger = LoggerFactory.getLogger(MethodTimerInterceptor.class);
  private final MeterRegistry registry;

  public MethodTimerInterceptor(MeterRegistry registry) {
    this.registry = registry;
  }

  @Override
  public boolean outgoingResponse(RequestDetails requestDetails, ResponseDetails responseDetails, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    logger.debug(requestDetails.getRequestId() +": " +requestDetails.getRequestType() + ", " + requestDetails.getRequestPath() + ", " + requestDetails.getRequestStopwatch().getMillis());
    Timer timer = Timer.builder("hjemmemonitorering_operation_timer")
        .description("Timers for hapi fhir operations")
        .tags( getTags(requestDetails) )
        .register(registry);
    timer.record( Duration.ofMillis(requestDetails.getRequestStopwatch().getMillis()) );

    return true;
  }

  private Iterable<Tag> getTags(RequestDetails requestDetails) {
    List<Tag> tags = new LinkedList<>();

    tags.add(Tag.of("type", requestDetails.getRequestType().toString()));
    if (requestDetails.getRestOperationType() == RestOperationTypeEnum.METADATA) {
      tags.add(Tag.of("resource", requestDetails.getOperation()));
    }
    else if (requestDetails.getRestOperationType() == RestOperationTypeEnum.TRANSACTION) {
      Bundle bundle = (Bundle)requestDetails.getResource();
      List<String> resources = bundle.getEntry().stream()
          .map(e -> e.getResource().getResourceType().toString())
          .sorted()
          .collect(Collectors.toList())
      ;
      tags.add(Tag.of("bundle_resources", String.join("_", resources)));
    }
    else {
      tags.add(Tag.of("resource", requestDetails.getResourceName()));
    }

    return tags;
  }
}
