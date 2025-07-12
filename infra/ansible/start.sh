#!/bin/bash
set -e

cd ~/app || exit 1

# Export secrets as environment variables
export MONGO_URI=$(echo "$SECRETS_JSON" | jq -r '.mongo_uri')
export HUGGINGFACEHUB_API_TOKEN=$(echo "$SECRETS_JSON" | jq -r '.huggingface_api_key')
export POSTGRES_USER=$(echo "$SECRETS_JSON" | jq -r '.postgres_user')
export POSTGRES_PASSWORD=$(echo "$SECRETS_JSON" | jq -r '.postgres_password')
export KC_DB_USERNAME=$POSTGRES_USER
export KC_DB_PASSWORD=$POSTGRES_PASSWORD
export KEYCLOAK_ADMIN=$(echo "$SECRETS_JSON" | jq -r '.keycloak_admin')
export KEYCLOAK_ADMIN_PASSWORD=$(echo "$SECRETS_JSON" | jq -r '.keycloak_admin_password')
export MONGO_INITDB_ROOT_USERNAME=$(echo "$SECRETS_JSON" | jq -r '.mongo_root_username')
export MONGO_INITDB_ROOT_PASSWORD=$(echo "$SECRETS_JSON" | jq -r '.mongo_root_password')
export ME_CONFIG_MONGODB_ADMINUSERNAME=$MONGO_INITDB_ROOT_USERNAME
export ME_CONFIG_MONGODB_ADMINPASSWORD=$MONGO_INITDB_ROOT_PASSWORD
export ME_CONFIG_BASICAUTH_USERNAME=$(echo "$SECRETS_JSON" | jq -r '.mongo_express_user')
export ME_CONFIG_BASICAUTH_PASSWORD=$(echo "$SECRETS_JSON" | jq -r '.mongo_express_password')

echo "Setting up OpenAPI config files..."
sh api/scripts/setup.sh

echo "[+] Building and starting Docker containers..."
docker compose up --env-file .env -d --build
