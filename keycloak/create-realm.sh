#!/bin/sh
set -e

KCADM_PATH="/opt/keycloak/bin/kcadm.sh"
KC_PATH="/opt/keycloak/bin/kc.sh"
CLIENT_ROLES=("client_user" "client_admin")

# Start Keycloak in background
$KC_PATH start-dev --http-port=9001 &

# Wait for Keycloak to become responsive to kcadm
echo "Waiting for Keycloak to become ready..."
until $KCADM_PATH config credentials \
  --server http://localhost:9001 \
  --realm master \
  --user "$KEYCLOAK_ADMIN" \
  --password "$KEYCLOAK_ADMIN_PASSWORD"; do
    echo "Keycloak not ready yet, retrying..."
    sleep 5
done

echo "Logged into Keycloak CLI"

# Create realm if it doesn't exist
if ! $KCADM_PATH get realms | grep '"realm" : "forms-ai"' > /dev/null; then
  $KCADM_PATH create realms -s realm=forms-ai -s enabled=true
  echo "Realm 'forms-ai' created"
else
  echo "Realm 'forms-ai' already exists"
fi

# Create 'user-service' confidential client if it doesn't exist
if ! $KCADM_PATH get clients -r forms-ai | grep "\"clientId\" : \"$KEYCLOAK_FORMSAI_USER\"" > /dev/null; then
  echo "Creating client 'user-service'..."
  $KCADM_PATH create clients -r forms-ai \
    -s clientId=$KEYCLOAK_FORMSAI_USER \
    -s name="User Service" \
    -s enabled=true \
    -s serviceAccountsEnabled=true \
    -s directAccessGrantsEnabled=true \
    -s publicClient=false \
    -s protocol=openid-connect \
    -s secret=$KEYCLOAK_FORMSAI_PASSWORD

  # Create realm role 'spring' if it doesn't exist
  if ! $KCADM_PATH get roles -r forms-ai | grep '"name" : "spring"' > /dev/null; then
    echo "Creating realm role 'spring'..."
    $KCADM_PATH create roles -r forms-ai -s name=spring
  fi

  # Assign realm role 'spring'
  $KCADM_PATH add-roles -r forms-ai --uusername service-account-$KEYCLOAK_FORMSAI_USER --rolename spring

  # Assign view-users and manage-users to service account
  $KCADM_PATH add-roles -r forms-ai \
    --uusername service-account-$KEYCLOAK_FORMSAI_USER \
    --cclientid realm-management \
    --rolename view-users

  $KCADM_PATH add-roles -r forms-ai \
    --uusername service-account-$KEYCLOAK_FORMSAI_USER \
    --cclientid realm-management \
    --rolename manage-users

  echo "Role assignment to service account succeeded!"

else
  echo "Client $KEYCLOAK_FORMSAI_USER already exists"
fi

# Create Angular frontend client if it doesn't exist
if ! $KCADM_PATH get clients -r forms-ai | grep '"clientId" : "angular-frontend"' > /dev/null; then
  echo "Creating client 'angular-frontend'..."

  $KCADM_PATH create clients -r forms-ai \
  -s clientId=angular-frontend \
  -s name="Angular Frontend" \
  -s enabled=true \
  -s publicClient=true \
  -s directAccessGrantsEnabled=true \
  -s standardFlowEnabled=true \
  -s protocol=openid-connect \
  -s rootUrl="http://localhost:4200"

  CLIENT_UUID=$($KCADM_PATH get clients -r forms-ai --fields id,clientId | \
    grep -B1 "\"clientId\" : \"angular-frontend\"" | \
    grep '"id"' | sed 's/.*"id" : "\(.*\)".*/\1/')

  for ROLE in "${CLIENT_ROLES[@]}"; do
    $KCADM_PATH create clients/$CLIENT_UUID/roles -r forms-ai -s name=$ROLE
  done

  $KCADM_PATH add-roles \
    -r forms-ai \
    --rname default-roles-forms-ai \
    --cclientid angular-frontend \
    --rolename client_user
else
  echo "Client 'angular-frontend' already exists"
fi

# Create mock user if it doesn't exist
if ! $KCADM_PATH get users -r forms-ai -q username=$KEYCLOAK_MOCK_USER | grep "\"username\" : \"$KEYCLOAK_MOCK_USER\"" > /dev/null; then
  echo "Creating mock user '$KEYCLOAK_MOCK_USER'..."

  $KCADM_PATH create users -r forms-ai \
    -s username=$KEYCLOAK_MOCK_USER \
    -s email=$KEYCLOAK_MOCK_USER_EMAIL \
    -s enabled=true \
    -s emailVerified=true \
    -s firstName="$KEYCLOAK_MOCK_USER_FIRST_NAME" \
    -s lastName="$KEYCLOAK_MOCK_USER_LAST_NAME"

  # Get the mock user's ID
  MOCK_USER_ID=$($KCADM_PATH get users -r forms-ai -q username=$KEYCLOAK_MOCK_USER --fields id --format csv | tail -n1 | tr -d '\r"')

  # Set password for mock user
  $KCADM_PATH set-password -r forms-ai --userid $MOCK_USER_ID --new-password $KEYCLOAK_MOCK_USER_PASSWORD

  # Optionally assign realm role 'spring'
  $KCADM_PATH add-roles -r forms-ai --uid $MOCK_USER_ID --rolename spring

  echo "Mock user '$KEYCLOAK_MOCK_USER' created and role assigned"
else
  echo "Mock user '$KEYCLOAK_MOCK_USER' already exists"
fi

# Keep container running
tail -f /dev/null
