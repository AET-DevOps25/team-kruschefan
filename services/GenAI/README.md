# GenAI  

GenAI is a FastAPI-based backend service that transforms natural language prompts into structured JSON forms using large language models (LLMs). It works with forms-ai to allow smooth generation of form data based on user prompts.

---

## Features

- Converts natural language into JSON-based forms
- Uses external LLMs via the OpenUI API
- Mocked LLM responses featured
- Docker-ready for deployment

---

## Installation

### Prerequisites

- Python 3.11+
- OpenUI API Key
- Docker (optional)

### Local Setup

```bash
git clone <repo-url>
cd GenAI
pip install -r requirements.txt
uvicorn main:app --reload
```

Set your API key environment variable:

```bash
export OPENUI_API_KEY=<your_openui_api_key>
```

---

## API Usage

### Endpoint: `POST /generate_form`

This endpoint generates a form schema from a natural language prompt.

#### Request

```json
{
  "prompt": "Create a form to gather feedback after an event.",
  "model": "deepseek-r1:70b"
}
```

#### Response

```json
{
  "title": "Event Feedback",
  "questions": [
    {
      "label": "How would you rate the overall organization of the event?",
      "type": "Single Choice",
      "options": ["Excellent", "Good", "Fair", "Poor"]
    },
    {
      "label": "What did you like most about the event?",
      "type": "Text",
      "options": null
    }
  ]
}
```

---

## Monitoring

Prometheus metrics are available at:

```
GET /metrics
```

### Exposed metrics:

- `llm_requests_total`
- `llm_request_failures_total`
- `form_generation_requests_total`
- `form_generation_failures_total`
- `form_generation_question_count`
- `llm_model_selected_total`

---

## Testing

Run the test suite using:

```bash
pytest
```

The `test.py` file includes:

- Mocked LLM responses
- Validation for required keys and structure
- Checks for proper field types and values

---

## Docker

### Build Docker Image

```bash
docker build -t genai-form-generator .
```

### Run the Container

```bash
docker run -e OPENUI_API_KEY=<your_openui_api_key> -p 8000:8000 genai-form-generator
```

---

## Project Structure

```
GenAI/
├── main.py              # FastAPI application
├── test.py              # Pytest test cases
├── requirements.txt     # Python dependencies
├── Dockerfile           # Multi-stage Docker build
└── llm/                 # Model-related config/docs
```

---

## Environment Variables

| Variable           | Description                                  | Required |
|--------------------|----------------------------------------------|----------|
| `OPENUI_API_KEY`   | API key for authenticating with OpenUI API   | Yes       |
| `OPENUI_HOSTNAME`  | Optional override for the API base URL       | No       |

Default hostname: `https://gpu.aet.cit.tum.de`
