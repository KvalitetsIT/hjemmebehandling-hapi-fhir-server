#!/bin/bash

if docker pull kvalitetsit/hjemmebehandling-hapi-fhir-server-documentation:latest; then
    echo "Copy from old documentation image."
    docker cp $(docker create kvalitetsit/hjemmebehandling-hapi-fhir-server-documentation:latest):/usr/share/nginx/html target/old
fi
