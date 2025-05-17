FROM python:3.11-slim

WORKDIR /app

# System dependencies for llama-cpp
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    libopenblas-dev \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Download the mistral model
RUN mkdir -p /app/llm && \
    wget -O /app/llm/mistral-7b-instruct-v0.1.Q2_K.gguf \
    https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.1-GGUF/resolve/main/mistral-7b-instruct-v0.1.Q2_K.gguf?download=true

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
