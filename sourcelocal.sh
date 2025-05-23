#!/bin/bash
set -e

cd "$(dirname "$0")" || exit 1

BUILD=${1:-nobuild}

if [ ! -f .env.secret ]; then
  echo "[!] .env.secret file not found. Please create one with your secrets."
  exit 1
fi

echo "[+] Loading secrets from .env.secret..."
export $(grep -v '^#' .env.secret | xargs)

# Derived environment variables (reuse)
export KC_DB_USERNAME=$POSTGRES_USER
export KC_DB_PASSWORD=$POSTGRES_PASSWORD
export ME_CONFIG_MONGODB_ADMINUSERNAME=$MONGO_ROOT_USERNAME
export ME_CONFIG_MONGODB_ADMINPASSWORD=$MONGO_ROOT_PASSWORD

echo "[+] All environment variables sourced."


# Usage
# source sourcelocal.sh
