package dk.kvalitetsit.hjemmebehandling.integrationtest;

import com.github.dockerjava.api.model.VolumesFrom;
import dk.kvalitetsit.hjemmebehandling.HapiFhirServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;

import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Collections;

public class ServiceStarter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarter.class);
    private static final Logger serviceLogger = LoggerFactory.getLogger("hjemmebehandling-hapi-fhir-server");
    private static final Logger postgresqlLogger = LoggerFactory.getLogger("postgresql");

    private Network dockerNetwork;
    private String jdbcUrl;

    public void startServices() {
        logger.info("Starting services...");
        dockerNetwork = Network.newNetwork();

        setupDatabaseContainer();
        logger.info("Database successfully started");

        System.setProperty("base.url", "http://localhost:8080/fhir");
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", "hapi");
        System.setProperty("spring.datasource.password", "hapi");
        System.setProperty("spring.batch.job.enabled", "false");
        System.setProperty("spring.datasource.driverClassName", "org.postgresql.Driver");
        System.setProperty("spring.batch.job.enabled", "false");
        System.setProperty("elasticsearch.enabled", "false");
        System.setProperty("spring.autoconfigure.exclude", "org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration");
        System.setProperty("version", "1.0");

        SpringApplication.run((HapiFhirServer.class));
    }

    public GenericContainer startServicesInDocker() {
        dockerNetwork = Network.newNetwork();

        setupDatabaseContainer();

        var resourcesContainerName = "hjemmebehandling-hapi-fhir-server-resources";
        var resourcesRunning = containerRunning(resourcesContainerName);
        logger.info("Resource container is running: " + resourcesRunning);

        GenericContainer service;

        // Start service
        if (resourcesRunning) {
            VolumesFrom volumesFrom = new VolumesFrom(resourcesContainerName);
            service = new GenericContainer<>("local/hjemmebehandling-hapi-fhir-server-qa:dev")
                    .withCreateContainerCmdModifier(modifier -> modifier.withVolumesFrom(volumesFrom))
                    .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true,append=true -cp integrationtest.jar");
        } else {
            service = new GenericContainer<>("local/hjemmebehandling-hapi-fhir-server-qa:dev")
                    .withFileSystemBind("/tmp", "/jacoco-report/")
                    .withEnv("JVM_OPTS", "-javaagent:/jacoco/jacocoagent.jar=output=file,destfile=/jacoco-report/jacoco-it.exec,dumponexit=true -cp integrationtest.jar");
        }

        service.withNetwork(dockerNetwork)
                .withNetworkAliases("hjemmebehandling-hapi-fhir-server")

                .withEnv("LOG_LEVEL", "INFO")

                .withEnv("spring.datasource.url", "jdbc:postgresql://postgresql:3306/hapi")
                .withEnv("spring.datasource.username", "hapi")
                .withEnv("spring.datasource.password", "hapi")

                .withEnv("spring.flyway.locations", "classpath:db/migration,filesystem:/app/sql")

//                .withEnv("JVM_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000")

                .withExposedPorts(8081, 8080)
                .waitingFor(Wait.forHttp("/actuator").forPort(8081).forStatusCode(200).withStartupTimeout(Duration.ofMinutes(3)));
        service.start();
        attachLogger(serviceLogger, service);

        return service;
    }

    private boolean containerRunning(String containerName) {
        return !DockerClientFactory
                .instance()
                .client()
                .listContainersCmd()
                .withNameFilter(Collections.singleton(containerName))
                .exec().isEmpty();
    }

    private void setupDatabaseContainer() {
        // Database server for Organisation.
        PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("hapi")
                .withUsername("hapi")
                .withPassword("hapi");

        postgresql.withNetwork(dockerNetwork)
                .withNetworkAliases("postgresql");
        postgresql.start();
        jdbcUrl = postgresql.getJdbcUrl();
        attachLogger(postgresqlLogger, postgresql);
    }

    private void attachLogger(Logger logger, GenericContainer container) {
        ServiceStarter.logger.info("Attaching logger to container: " + container.getContainerInfo().getName());
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        container.followOutput(logConsumer);
    }
}
