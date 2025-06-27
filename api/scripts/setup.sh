#!/bin/bash
set -e

OPENAPI_SRC="api/openapi.yaml"
SERVICES_DIR="services"

for service in "$SERVICES_DIR"/*; do
  if [ -d "$service" ]; then
    if [ -f "$service/pom.xml" ] || [ -f "$service/build.gradle" ]; then
      RESOURCES_DIR="$service/src/main/resources"
      mkdir -p "$RESOURCES_DIR"
      cp "$OPENAPI_SRC" "$RESOURCES_DIR/openapi.yaml"
    fi
  fi
done