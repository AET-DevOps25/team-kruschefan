from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from langchain.prompts import PromptTemplate
from langchain_huggingface import HuggingFaceEndpoint
from langchain.llms import LlamaCpp
from pydantic import BaseModel
from typing import Optional, List
from enum import Enum
import os
import re
import json
import logging

app = FastAPI()
HUGGINGFACEHUB_API_TOKEN = os.getenv("HUGGINGFACE_API_KEY")
REMOTE_REPO_ID = "HuggingFaceH4/zephyr-7b-beta"
LOCAL_REPO_ID = "TheBloke/OpenHermes-2.5-Mistral-7B-GGUF"


# Classes
class FormRequest(BaseModel):
    prompt: str
    model_source: Optional[str] = "remote"
    llm_name: Optional[str] = None


class QuestionType(str, Enum):
    TEXT = 'Text'
    DATE = 'Date'
    NUMBER = 'Number'
    MULTIPLE_CHOICE = 'Multiple Choice'
    COMMENT = 'Comment'
    TEXT_BOX = 'Text Box'
    SINGLE_CHOICE = 'Single Choice'
    DROPDOWN = 'Dropdown'

class Question(BaseModel):
    label: str
    type: QuestionType

class GenAIResponse(BaseModel):
    title: str
    questions: List[Question]


def load_llm(model_source, llm_name):
    if model_source == "local":
        model_path = llm_name or "./llm/mistral-7b-instruct-v0.1.Q2_K.gguf"
        model_path = os.path.expanduser(model_path)
        return LlamaCpp(
            model_path=model_path,
            temperature=0.2,
            max_tokens=1024,
            n_ctx=2048
        )
    elif model_source == "remote":
        return HuggingFaceEndpoint(
            repo_id=llm_name or REMOTE_REPO_ID,
            temperature=0.2,
            huggingfacehub_api_token=HUGGINGFACEHUB_API_TOKEN,
        )
    else:
        raise ValueError("Invalid model source")


@app.post("/generate_form", response_model=GenAIResponse)
async def generate_form(request: FormRequest):
    user_prompt = request.prompt
    model_source = request.model_source or "local"
    llm_name = request.llm_name

    print("Prompt received:", user_prompt)
    print("Using model:", llm_name or ("default " + model_source))

    llm = load_llm(model_source, llm_name)

    generation_prompt = PromptTemplate.from_template(
        "You are a helpful assistant for building dynamic forms."
        "A user wants to create a form based on the following description:"
        "{question}"
        "Generate a JSON object representing this form. The object must have two keys: 'title' (a short string, max 50 chars) and 'questions' (an array of form questions)."
        "The 'questions' array must contain between 4 and 8 question objects."
        "Each question object in the 'questions' array must have the following keys:"
        "- 'label': A string representing the name or label of the field."
        "- 'type': A string representing the type of the field. Choose one from the following exact values: 'Text', 'Date', 'Number', 'Multiple Choice', 'Comment', 'Text Box', 'Single Choice', 'Dropdown'."
        "Respond ONLY with a valid JSON object. Do not include any markdown, explanations, or additional text outside of the JSON object."
    )

    full_generation_prompt = generation_prompt.format(question=user_prompt)
    generated_json_string = llm.invoke(full_generation_prompt)

    try:
        parsed_data = json.loads(generated_json_string)
        return GenAIResponse(**parsed_data)
    except json.JSONDecodeError as e:
        logging.error(f"JSON Decode Error: {e} - Raw LLM output: {generated_json_string}")
        raise HTTPException(
            status_code=500,
            detail=f"Failed to parse AI response as valid JSON. Raw output: {generated_json_string[:200]}...",
        )
    except Exception as e:
        logging.error(f"Error validating AI response: {e} - Parsed data: {generated_json_string}")
        raise HTTPException(
            status_code=500,
            detail=f"AI response did not match expected form structure. Error: {e}",
        )