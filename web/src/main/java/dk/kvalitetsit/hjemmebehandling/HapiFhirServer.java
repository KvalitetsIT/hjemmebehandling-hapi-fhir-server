package dk.kvalitetsit.hjemmebehandling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HapiFhirServer {
	public static void main(String[] args)  {
		SpringApplication.run(HapiFhirServer.class, args);
	}
}
