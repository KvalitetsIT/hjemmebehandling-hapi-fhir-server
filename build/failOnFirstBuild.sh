#!/bin/sh

echo "${GITHUB_REPOSITORY}"
echo "${DOCKER_SERVICE}"
if [ "${GITHUB_REPOSITORY}" != "KvalitetsIT/hjemmebehandling-hapi-fhir-server" ] && [ "${DOCKER_SERVICE}" = "kvalitetsit/hjemmebehandling-hapi-fhir-server" ]; then
  echo "Please run setup.sh REPOSITORY_NAME"
  exit 1
fi
