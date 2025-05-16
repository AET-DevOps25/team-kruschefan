from fastapi import FastAPI, Body
from langchain.prompts import PromptTemplate
from langchain_huggingface import HuggingFaceEndpoint
import re
import json
import logging

app = FastAPI()
HUGGINGFACEHUB_API_TOKEN = "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"
REPO_ID = "HuggingFaceH4/zephyr-7b-beta"

# Dynamic form generation endpoint: uses Mistral to create fields
@app.post("/generate_form")
async def generate_form(data: dict = Body(..., example={"prompt": "I want to collect user preferences and ratings"})):
    user_prompt = data.get("prompt")
    print("Prompt received:", user_prompt)

    llm = HuggingFaceEndpoint(
        repo_id=REPO_ID,
        temperature=0.2,
        huggingfacehub_api_token=HUGGINGFACEHUB_API_TOKEN,
    )

    # Step 1: Generate form description
    generation_prompt = PromptTemplate.from_template(
        "<s>You are a helpful assistant for building dynamic forms.\n"
        "A user said:\n\n"
        "{question}\n\n"
        "Based on this, describe what components should be included in the form and why."
       )

    full_generation_prompt = generation_prompt.format(question=user_prompt)
    generated_description = llm.invoke(full_generation_prompt)
    print("Generated description:", generated_description)

    # Step 2: Extract structured fields
    structuring_prompt = PromptTemplate.from_template(
        "<s>You are a helpful assistant that extracts structured form fields from a description.\n\n"
        "Here is a description of the desired form:\n\n"
        "{description}\n\n"
        "Now extract a JSON array where each item contains:\n"
        "- label: name of the field\n"
        "- type: one of [text, boolean, rating, date, select, number, file, email, phone, textarea]\n"
        "- purpose: what this field is for\n\n"
        "Do not include markdown, explanation, or any text outside of the array."
        "Respond ONLY with a valid JSON array. Do not include markdown, explanations, or any text outside the array."
    )
    full_structuring_prompt = structuring_prompt.format(description=generated_description)
    structured_output = llm.invoke(full_structuring_prompt)
    print("Structured output: " + structured_output)

    # Try parsing JSON (TODO: first returned JSON)
    match = re.search(r"\[\s*{.*?}\s*\]", structured_output, re.DOTALL)
    if match:
        try:
            fields = json.loads(match.group(0))
        except Exception as e:
            logging.exception("Failed to parse extracted JSON")
            return {
                "error": "Failed to parse JSON from extracted array.",
                "raw_output": structured_output,
                "matched_text": match.group(0),
                "exception": str(e)
            }
    else:
        return {
            "error": "No JSON array found in model output.",
            "raw_output": structured_output
        }


    return {
        "prompt": user_prompt,
        "description": generated_description,
        "field_count": len(fields),
        "fields": fields
    }
