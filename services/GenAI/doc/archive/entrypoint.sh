#!/bin/bash
set -e

MODEL_DIR=/models
MODEL_FILE=mistral-7b-instruct-v0.1.Q2_K.gguf
REPO=TheBloke/Mistral-7B-Instruct-v0.1-GGUF

# Ensure model directory exists
mkdir -p "$MODEL_DIR"

# Download model if not already there
if [ ! -f "$MODEL_DIR/$MODEL_FILE" ]; then
  echo "Downloading model from $REPO..."
  
  if [ -z "$HUGGINGFACE_API_KEY" ]; then
    echo "Error: HUGGINGFACE_API_KEY is not set"
    exit 1
  fi

  huggingface-cli login --token "$HUGGINGFACE_API_KEY"
  
  huggingface-cli download "$REPO" \
    --local-dir "$MODEL_DIR" \
    --repo-type model

  # Optionally, keep only the desired model file
  find "$MODEL_DIR" -type f ! -name "$MODEL_FILE" -delete
else
  echo "Model already exists. Skipping download."
fi

# Start your FastAPI app
exec uvicorn main:app --host 0.0.0.0 --port 8000
