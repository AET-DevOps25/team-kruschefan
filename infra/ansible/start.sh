#!/bin/bash
set -e

cd /home/ec2-user/app || exit 1

echo "Setting up OpenAPI config files..."
sh api/scripts/setup.sh

echo "Sourcing env vars..."
source sourcelocal.sh

echo "[+] Building Docker containers..."
docker compose build
