package dk.kvalitetsit.hjemmebehandling.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;


@Service
public class DatabaseHealthService {
  @Autowired
  private DataSource dataSource;

  private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthService.class);

  public boolean isHealthy() {
    try {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeQuery("select 1");
            return true;
        } catch (Exception e) {
            logger.error("Error quering database for health check.", e);
            return false;
        }
    }
    catch(Exception e) {
      logger.error("Error getting connection for database query.", e);
      return false;
    }
  }
}
