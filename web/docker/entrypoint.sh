#! /bin/bash

if [ "$TZ" = "" ]
then
   echo "Using default timezone (UTC)"
fi

if [[ -z $LOGGING_CONFIG ]]; then
  echo "Default logback configuration file: /home/appuser/logback-spring.xml"
  export LOGGING_CONFIG=/home/appuser/logback-spring.xml
fi

if [[ -z $LOG_LEVEL ]]; then
  echo "Default LOG_LEVEL = INFO"
  export LOG_LEVEL=INFO
fi

if [[ -z $HAPI_LOG_LEVEL ]]; then
  echo "Default HAPI_LOG_LEVEL = WARN"
  export HAPI_LOG_LEVEL=WARN
fi

if [[ -z $HIBERNATE_LOG_LEVEL ]]; then
  echo "Default HIBERNATE_LOG_LEVEL = ERROR"
  export HIBERNATE_LOG_LEVEL=ERROR
fi

if [[ -z $LOG_LEVEL_FRAMEWORK ]]; then
  echo "Default LOG_LEVEL_FRAMEWORK = INFO"
  export LOG_LEVEL_FRAMEWORK=INFO
fi

if [[ -z $CORRELATION_ID ]]; then
  echo "Default CORRELATION_ID = correlation-id"
  export CORRELATION_ID=x-request-id
fi

if [[ -z $SERVICE_ID ]]; then
  echo "Default SERVICE_ID = hjemmebehandling-hapi-fhir-server"
  export SERVICE_ID=hjemmebehandling-hapi-fhir-server
fi

JAR_FILE=web.jar

echo "Starting service with the following command."
echo "java $JVM_OPTS -jar $JAR_FILE"

# start the application
exec java $JVM_OPTS -jar $JAR_FILE
