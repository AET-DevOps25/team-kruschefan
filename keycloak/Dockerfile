FROM quay.io/keycloak/keycloak:24.0.1

USER root

# Install curl (needed for readiness checks)
RUN microdnf install -y curl && microdnf clean all

# Copy your init script into the image
COPY init-keycloak.sh /opt/keycloak/init-keycloak.sh
RUN chmod +x /opt/keycloak/init-keycloak.sh

USER 1000

# Set script as entrypoint
ENTRYPOINT ["/opt/keycloak/init-keycloak.sh"]
