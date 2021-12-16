package dk.kvalitetsit.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HealthServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Value("${version}")
  private String version;

  @Autowired
  private DatabaseHealthService databaseHealth;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HealthStatus healthStatus = getHealth();
    response.setContentType("application/json");
    response.getOutputStream().write(healthStatus.status.getBytes());
    response.setStatus(healthStatus.healthy ? 200 : 500);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HealthStatus healthStatus = getHealth();
    response.setContentType("application/json");
    response.getOutputStream().write(healthStatus.status.getBytes());
    response.setStatus(healthStatus.healthy ? 200 : 500);
  }

  public HealthStatus getHealth() {
    boolean healthy = databaseHealth.isHealthy();
    return new HealthStatus(healthy, "{ \"version\": \"" + version + "\", "
        + "\"database\": \"" + healthy + "\"}");
  }

  class HealthStatus {
    private HealthStatus(boolean healthy, String status) {
      this.healthy = healthy;
      this.status = status;
    }
    boolean healthy;
    String status;
  }
}
