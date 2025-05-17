from fastapi import FastAPI, Body
from langchain.prompts import PromptTemplate
from langchain_huggingface import HuggingFaceEndpoint
from langchain_community.llms import HuggingFacePipeline
from langchain.chains import LLMChain, RetrievalQA
from langchain_community.vectorstores import Weaviate
from langchain.embeddings import HuggingFaceEmbeddings
from langchain.llms import LlamaCpp
from pydantic import BaseModel
from typing import Optional
import weaviate
import os
import re
import json
import logging

app = FastAPI()
HUGGINGFACEHUB_API_TOKEN = "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"
REMOTE_REPO_ID = "HuggingFaceH4/zephyr-7b-beta"
LOCAL_REPO_ID = "TheBloke/OpenHermes-2.5-Mistral-7B-GGUF"    # TODO: change


# Classes
class FormRequest(BaseModel):
    prompt: str
    model_source: Optional[str] = "remote"
    llm_name: Optional[str] = None

class IngestRequest(BaseModel):
    content: str
    metadata: Optional[dict] = None  # Optional metadata you might want to store later


# Functions
# TODO: write errors
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
        raise ValueError("Invalid model source") # TODO: throw error and quit

def get_weaviate_retriever():
    client = weaviate.Client("http://weaviate:8080")

    embeddings = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")

    vectorstore = Weaviate(
        client=client,
        index_name="FormDocs",
        text_key="content",
        embedding=embeddings,
        by_text=False
    )

    # Define schema
    class_obj = {
        "class": "FormDocs",
        "description": "Dynamic forms and content",
        "vectorizer": "none",  # You are providing embeddings manually
        "properties": [
            {
                "name": "content",
                "dataType": ["text"]
            },
        ]
    }

    # Create it if not exists
    if not client.schema.contains(class_obj):
        client.schema.create_class(class_obj)

    return vectorstore.as_retriever()
    


@app.post("/ingest_doc")
async def ingest_doc(request: IngestRequest):
    client = weaviate.Client("http://weaviate:8080")

    embeddings = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")
    vector = embeddings.embed_query(request.content)

    data_object = {
        "content": request.content
    }

    try:
        client.data_object.create(
            data_object=data_object,
            class_name="FormDocs",
            vector=vector
        )
        return {"status": "success", "message": "Document ingested successfully."}
    except Exception as e:
        logging.exception("Failed to ingest document")
        return {
            "status": "error",
            "message": str(e)
        }


# Dynamic form generation endpoint: uses Mistral to create fields
@app.post("/generate_form")
async def generate_form(request: FormRequest):
    user_prompt = request.prompt
    model_source = request.model_source
    llm_name = request.llm_name

    print("Prompt received:", user_prompt)
    print("Using model:", llm_name or ("default " + model_source))

    llm = load_llm(model_source, llm_name)
    retriever = get_weaviate_retriever()

    qa_chain = RetrievalQA.from_chain_type(llm=llm, retriever=retriever)

    # Step 1: Generate form description
    generation_prompt = PromptTemplate.from_template(
        "<s>[INST] You are a helpful assistant for building dynamic forms.\n"
        "A user said:\n\n"
        "{question}\n\n"
        "Based on this, describe what components should be included in the form and why."
        "Now extract a JSON array where each item contains:\n"
        "- label: name of the field\n"
        "- type: one of [text, boolean, rating, date, select, number, file, email, phone, textarea]\n"
        "- purpose: what this field is for\n\n"
        "Do not include markdown, explanation, or any text outside of the array."
        "Respond ONLY with a valid JSON array. Do not include markdown, explanations, or any text outside the array. [/INST]</s>"
    )

    full_generation_prompt = generation_prompt.format(question=user_prompt)
    #generated_description = llm.invoke(full_generation_prompt)
    generated_description = qa_chain.run(full_generation_prompt)
    print("Prompt: ", full_generation_prompt)
    print("Generated description:", generated_description)

    # Step 2: Extract structured fields
    # TODO: fix, 2 step does not work, but 1 step works well enough
    """
    structuring_prompt = PromptTemplate.from_template(
        "You are a helpful assistant that extracts structured form fields from a description.\n\n"
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
    print("Structured prompt: " + full_structuring_prompt)
    structured_output = llm.invoke(full_structuring_prompt)
    print("Structured output: " + structured_output)
    """
    structured_output = generated_description   #TODO: del

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
