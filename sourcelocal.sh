#!/usr/bin/env bash
cd "$(dirname "${BASH_SOURCE[0]}")" || exit 1

BUILD="${1:-nobuild}"

if [[ ! -f .env.secret ]]; then
  echo "[!] .env.secret file not found. Please create one with your secrets."
  exit 1
fi

echo "[+] Loading secrets from .env.secret..."
while IFS= read -r line || [[ -n "$line" ]]; do
  # Skip comments and empty lines
  if [[ "$line" =~ ^# ]] || [[ -z "$line" ]]; then
    continue
  fi

  # Split into name and value
  IFS='=' read -r name value <<< "$line"

  # Export the variable
  export "$name"="$value"
done < .env.secret

echo "[+] All environment variables sourced."
