#!/bin/sh
set -e

# Start Keycloak in background
/opt/keycloak/bin/kc.sh start-dev &

# Wait for Keycloak to become responsive to kcadm
echo "Waiting for Keycloak to become ready..."
until /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user "$KEYCLOAK_ADMIN" \
  --password "$KEYCLOAK_ADMIN_PASSWORD"; do
    echo "Keycloak not ready yet, retrying..."
    sleep 5
done

echo "Logged into Keycloak CLI"

# Create realm if it doesn't exist
if ! /opt/keycloak/bin/kcadm.sh get realms | grep '"realm" : "forms-ai"' > /dev/null; then
  /opt/keycloak/bin/kcadm.sh create realms -s realm=forms-ai -s enabled=true
  echo "Realm 'forms-ai' created"
else
  echo "Realm 'forms-ai' already exists"
fi

# Create user 'keycloak' in forms-ai
if ! /opt/keycloak/bin/kcadm.sh get users -r forms-ai | grep '"username" : "keycloak"' > /dev/null; then
  echo "Creating user 'keycloak' in realm 'forms-ai'..."
  /opt/keycloak/bin/kcadm.sh create users -r forms-ai \
    -s username=keycloak \
    -s enabled=true \
    -s email=keycloak@example.com \
    -s firstName=Key \
    -s lastName=Cloak

  ID=$( /opt/keycloak/bin/kcadm.sh get users -r forms-ai -q username=keycloak --fields id --format csv | tail -n1 | tr -d '\r"' )
  /opt/keycloak/bin/kcadm.sh set-password -r forms-ai --userid "$ID" --new-password keycloak-pw --temporary=false

  /opt/keycloak/bin/kcadm.sh update users/$ID -r forms-ai \
    -s emailVerified=true \
    -s enabled=true \
    --set 'requiredActions=[]'
fi

# Create 'user-service' confidential client
if ! /opt/keycloak/bin/kcadm.sh get clients -r forms-ai | grep '"clientId" : "user-service"' > /dev/null; then
  echo "Creating client 'user-service'..."
  /opt/keycloak/bin/kcadm.sh create clients -r forms-ai \
    -s clientId=user-service \
    -s name="User Service" \
    -s enabled=true \
    -s serviceAccountsEnabled=true \
    -s directAccessGrantsEnabled=true \
    -s publicClient=false \
    -s protocol=openid-connect \
    -s secret="user-service-secret"

  CLIENT_ID=$( /opt/keycloak/bin/kcadm.sh get clients -r forms-ai -q clientId=user-service --fields id --format csv | tail -n1 | tr -d '\r"' )

  # Create role 'spring' if not exists
  if ! /opt/keycloak/bin/kcadm.sh get roles -r forms-ai | grep '"name" : "spring"' > /dev/null; then
    echo "Creating realm role 'spring'..."
    /opt/keycloak/bin/kcadm.sh create roles -r forms-ai -s name=spring
  fi

  # Assign realm role 'spring' to service account of 'user-service'
  SERVICE_ACCOUNT_ID=$( /opt/keycloak/bin/kcadm.sh get users -r forms-ai -q username=service-account-user-service --fields id --format csv | tail -n1 | tr -d '\r"' )
  /opt/keycloak/bin/kcadm.sh add-roles -r forms-ai --uusername service-account-user-service --rolename spring
fi

# Replace current shell with Keycloak to keep container running
tail -f /dev/null
