#!/bin/bash
set -e

cd /home/ec2-user/app || exit 1

echo "Setting up OpenAPI config files..."
sh app/api/scripts/setup.sh
