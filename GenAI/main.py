from fastapi import FastAPI
from langchain.prompts import PromptTemplate
from langchain_huggingface import HuggingFaceEndpoint
from langchain.llms import LlamaCpp
from pydantic import BaseModel
from typing import Optional
import os
import re
import json
import logging

app = FastAPI()
HUGGINGFACEHUB_API_TOKEN = os.getenv("HUGGINGFACE_API_KEY")
REMOTE_REPO_ID = "HuggingFaceH4/zephyr-7b-beta"
LOCAL_REPO_ID = "TheBloke/OpenHermes-2.5-Mistral-7B-GGUF"  # TODO: change


# Classes
class FormRequest(BaseModel):
    prompt: str
    model_source: Optional[str] = "remote"
    llm_name: Optional[str] = None


# Functions
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


# Dynamic form generation endpoint
@app.post("/generate_form")
async def generate_form(request: FormRequest):
    user_prompt = request.prompt
    model_source = request.model_source
    llm_name = request.llm_name

    print("Prompt received:", user_prompt)
    print("Using model:", llm_name or ("default " + model_source))

    llm = load_llm(model_source, llm_name)

    generation_prompt = PromptTemplate.from_template(
        "You are a helpful assistant for building dynamic forms.\n"
        "A user said:\n\n"
        "{question}\n\n"
        "Based on this, describe what components should be included in the form and why."
        "Now extract a JSON array where each item contains:\n"
        "- label: name of the field\n"
        "- type: one of [text, boolean, rating, date, select, number, file, email, phone, textarea]\n"
        "- purpose: what this field is for\n\n"
        "Do not include markdown, explanation, or any text outside of the array."
        "Respond ONLY with a valid JSON array. Do not include markdown, explanations, or any text outside the array."
    )

    full_generation_prompt = generation_prompt.format(question=user_prompt)
    generated_description = llm.invoke(full_generation_prompt)

    print("Prompt: ", full_generation_prompt)
    print("Generated description:", generated_description)

    # Try parsing JSON
    match = re.search(r"\[\s*{.*?}\s*\]", generated_description, re.DOTALL)
    if match:
        try:
            fields = json.loads(match.group(0))
        except Exception as e:
            logging.exception("Failed to parse extracted JSON")
            return {
                "error": "Failed to parse JSON from extracted array.",
                "raw_output": generated_description,
                "matched_text": match.group(0),
                "exception": str(e)
            }
    else:
        return {
            "error": "No JSON array found in model output.",
            "raw_output": generated_description
        }

    return {
        "prompt": user_prompt,
        "description": generated_description,
        "field_count": len(fields),
        "fields": fields
    }
