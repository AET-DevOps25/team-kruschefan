from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List
from enum import Enum
import os
import re
import json
import logging
import requests
from prometheus_client import Counter, Histogram, make_asgi_app
import time


app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:4200"],  # or ["*"] for development
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
OPENUI_API_KEY = os.getenv("OPENUI_API_KEY")
OPENUI_HOSTNAME = "https://gpu.aet.cit.tum.de"
DEFAULT_MODEL = "deepseek-r1:70b"


# Prometheus Metrics
LLM_REQUESTS = Counter("llm_requests_total", "Total LLM requests")
LLM_FAILURES = Counter("llm_request_failures_total", "Failed LLM requests")
LLM_DURATION = Histogram("llm_request_duration_seconds", "LLM request duration")
MODEL_SELECTED = Counter("llm_model_selected_total", "Model used", ["model"])
FORM_REQUESTS = Counter("form_generation_requests_total", "Total form generation requests")
FORM_FAILURES = Counter("form_generation_failures_total", "Form generation failures")
FORM_QUESTION_COUNT = Histogram("form_generation_question_count", "Number of questions in form", buckets=[0, 2, 4, 6, 8, 10])

# Mount Prometheus /metrics endpoint
metrics_app = make_asgi_app()
app.mount("/metrics", metrics_app)

# Classes
class FormRequest(BaseModel):
    prompt: str
    model: Optional[str] = ""


class QuestionType(str, Enum):
    TEXT = "Text"
    DATE = "Date"
    NUMBER = "Number"
    MULTIPLE_CHOICE = "Multiple Choice"
    COMMENT = "Comment"
    TEXT_BOX = "Text Box"
    SINGLE_CHOICE = "Single Choice"
    DROPDOWN = "Dropdown"


class Question(BaseModel):
    label: str
    type: QuestionType
    options: Optional[List[str]] = None


class GenAIResponse(BaseModel):
    title: str
    questions: List[Question]


def select_model(model_name):
    models = get_models()
    if model_name not in models:
        model_name = models[0]
    return model_name


def get_models():
    url = f"{OPENUI_HOSTNAME}/api/models"
    headers = {"Authorization": f"Bearer {OPENUI_API_KEY}"}

    response = requests.get(url, headers=headers)

    if response.status_code != 200:
        print(f"Error: {response.status_code}")
        return {"error": "Failed to retrieve models"}

    try:
        data = response.json()
        print(data)

        models = [entry["id"] for entry in data.get("data", [])]
        return models
    except requests.exceptions.JSONDecodeError as e:
        print(f"Error parsing JSON: {e}")
        print(response.text)
        return {"error": "Failed to parse response"}


@app.post("/generate_form")
async def generate_form(request: FormRequest):
    # analytics
    FORM_REQUESTS.inc()
    start_time = time.time()

    user_prompt = request.prompt
    model = select_model(request.model)
    # analytics
    MODEL_SELECTED.labels(model=model).inc()


    print(f"Prompt received: {user_prompt}")
    print(f"Using model {model}")

    generation_prompt = f"""
        You are a helpful assistant for building dynamic forms.
        A user wants to create a form based on the following description:
        {user_prompt}
        Generate a JSON object representing this form. The object must have two keys: 'title' (a short string, max 50 chars) and 'questions' (an array of form questions).
        The 'questions' array must contain between 4 and 8 question objects.
        Each question object in the 'questions' array must have the following keys:
        - 'label': A string representing the question to be asked. It should be a conscise and clear question and should be min 20 chars.
        - 'type': A string representing the type of the field. Choose one STRICTLY from the following exact values: 'Text', 'Date', 'Number', 'Multiple Choice', 'Text Box', 'Single Choice', 'Dropdown'.
        - 'options': A list of string that should be ONLY generated for 'multiple choice', 'single choice' and 'dropdown'. Otherwise, set it as None.
        Respond ONLY with a valid JSON object. Do not include any markdown, explanations, or additional text outside of the JSON object.
    """

    url = f"{OPENUI_HOSTNAME}/api/chat/completions"
    headers = {
        "Authorization": f"Bearer {OPENUI_API_KEY}",
        "Content-Type": "application/json",
    }
    data = {
        "model": model,
        "messages": [{"role": "user", "content": generation_prompt}],
    }

    try:
        LLM_REQUESTS.inc()
        response = requests.post(url, headers=headers, json=data)
        duration = time.time() - start_time
        LLM_DURATION.observe(duration)

        # process output
        generated_json_string = response.json()["choices"][0]["message"]["content"]
        generated_json_string = re.sub(
            r"<think>.*?</think>", "", generated_json_string, flags=re.DOTALL
        ).strip()
        if generated_json_string.startswith("```json"):
            generated_json_string = generated_json_string.lstrip("`json").strip("`").strip()
        elif generated_json_string.startswith("```"):
            generated_json_string = generated_json_string.strip("`").strip()

        parsed_data = json.loads(generated_json_string)
        FORM_QUESTION_COUNT.observe(len(parsed_data.get("questions", [])))
        return GenAIResponse(**parsed_data)

    except json.JSONDecodeError as e:
        LLM_FAILURES.inc()
        FORM_FAILURES.inc()
        logging.error(f"JSON Decode Error: {e} - Raw LLM output: {generated_json_string}")
        raise HTTPException(
            status_code=500,
            detail=f"Failed to parse AI response as valid JSON. Raw output: {generated_json_string[:200]}...",
        )
    except Exception as e:
        LLM_FAILURES.inc()
        FORM_FAILURES.inc()
        logging.error(f"Error validating AI response: {e} - Parsed data: {generated_json_string}")
        raise HTTPException(
            status_code=500,
            detail=f"AI response did not match expected form structure. Error: {e}",
        )
