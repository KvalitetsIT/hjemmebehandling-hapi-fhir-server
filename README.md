![Build Status](https://github.com/KvalitetsIT/hjemmebehandling-hapi-fhir-server/workflows/CICD/badge.svg) ![Test Coverage](.github/badges/jacoco.svg)
# hjemmebehandling-hapi-fhir-server

The hapi-fhir-server-bff is a build of a standard Hapi Fhir server. This service works as an api server for both the patient- and employee BFFs   

The service is a Java Spring Boot application with the following properties

1. Exposes REST interfaces 
3. Is a FHIR server
4. Exposes metrics url for prometheus to scrape

## Prerequesites
To build the project you need:

 * OpenJDK 11+
 * Maven 3.1.1+
 * Docker 20+
 * Docker-compose 1.29+

## Building the service

The simplest way to build the service is to use maven:

```
mvn clean install
```

This will build the service as a JAR used by spring boot.

## Running the service
Use the local docker-compose setup

```
cd compose/
docker-compose up
```
This will start the hapi server and the bff.

## Logging

The service uses Logback for logging. At runtime Logback log levels can be configured via environment variable.

## Endpoints

The service is listening for connections on port 8080.

Spring boot actuator is listening for connections on port 8081. This is used as prometheus scrape endpoint and health monitoring. 

Prometheus scrape endpoint: `http://localhost:8083/actuator/prometheus`  
Health URL that can be used for readiness probe: `http://localhost:8083/actuator/health`. 

In DIAS the http://localhost:8082/fhir/metadata is used for health.

## Configuration

| Environment variable | Description | Required |
|----------------------|-------------|---------- |
| JDBC_URL | JDBC connection URL | Yes |
| JDBC_USER | JDBC user          | Yes |
| JDBC_PASS | JDBC password      | Yes |
| LOG_LEVEL | Log Level for applikation  log. Defaults to INFO. | No |
| LOG_LEVEL_FRAMEWORK | Log level for framework. Defaults to INFO. | No |
| CORRELATION_ID | HTTP header to take correlation id from. Used to correlate log messages. Defaults to "x-request-id". | No
