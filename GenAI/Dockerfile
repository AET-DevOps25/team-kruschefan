FROM python:3.11-slim

WORKDIR /app

# System dependencies for llama-cpp
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    build-essential \
    cmake \
    libopenblas-dev \
    wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt -v

COPY . . 

RUN rm -rf /root/.cache


RUN chmod +x entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]
