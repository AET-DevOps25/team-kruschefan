FROM python:3.11-slim

WORKDIR /app

COPY schema_loader.py .

RUN pip install pymongo

CMD ["python", "schema_loader.py"]
