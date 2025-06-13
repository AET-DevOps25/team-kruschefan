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

app = FastAPI()
OPENUI_API_KEY = os.getenv("OPENUI_API_KEY")
OPENUI_HOSTNAME = "https://gpu.aet.cit.tum.de"
DEFAULT_MODEL = "deepseek-r1:70b"


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
    user_prompt = request.prompt
    model = select_model(request.model)

    print(f"Prompt received: {user_prompt}")
    print(f"Using model {model}")

    generation_prompt = f"""
        You are a helpful assistant for building dynamic forms.
        A user wants to create a form based on the following description:
        {user_prompt}
        Generate a JSON object representing this form. The object must have two keys: 'title' (a short string, max 50 chars) and 'questions' (an array of form questions).
        The 'questions' array must contain between 4 and 8 question objects.
        Each question object in the 'questions' array must have the following keys:
        - 'label': A string representing the name or label of the field.
        - 'type': A string representing the type of the field. Choose one STRICTLY from the following exact values: 'Text', 'Date', 'Number', 'Multiple Choice', 'Comment', 'Text Box', 'Single Choice', 'Dropdown'.
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

    response = requests.post(url, headers=headers, json=data)

    print(response.status_code)  # TODO: error handling
    generated_json_string = response.json()["choices"][0]["message"]["content"]
    generated_json_string = re.sub(
        r"<think>.*?</think>", "", generated_json_string, flags=re.DOTALL
    ).strip()

    try:
        parsed_data = json.loads(generated_json_string)
        return GenAIResponse(**parsed_data)
    except json.JSONDecodeError as e:
        logging.error(
            f"JSON Decode Error: {e} - Raw LLM output: {generated_json_string}"
        )
        raise HTTPException(
            status_code=500,
            detail=f"Failed to parse AI response as valid JSON. Raw output: {generated_json_string[:200]}...",
        )
    except Exception as e:
        logging.error(
            f"Error validating AI response: {e} - Parsed data: {generated_json_string}"
        )
        raise HTTPException(
            status_code=500,
            detail=f"AI response did not match expected form structure. Error: {e}",
        )
