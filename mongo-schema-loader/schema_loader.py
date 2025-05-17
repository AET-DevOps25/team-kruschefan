from pymongo import MongoClient
import os

# MongoDB connection
mongo_uri = os.getenv("MONGO_URI", "mongodb://root:example@mongodb:27017")
client = MongoClient(mongo_uri)

# Database name
db = client["formbuilder"]

# === 1. Create 'forms' collection ===
if "forms" not in db.list_collection_names():
    db.create_collection("forms", validator={
        "$jsonSchema": {
            "bsonType": "object",
            "required": ["user_id", "title", "description", "data", "created_at"],
            "properties": {
                "user_id": { "bsonType": "string" },
                "title": { "bsonType": "string" },
                "description": { "bsonType": "string" },
                "form_data": { "bsonType": "object" }, # JSON
                "created_at": { "bsonType": "date" },
                "alert_recipients": { "bsonType": "object" },  # JSON
                "tags": {
                    "bsonType": "array",
                    "items": { "bsonType": "string" }
                }
            }
        }
    })
    print("Created 'forms' collection with validation schema")
else:
    print("'forms' collection already exists")

# === 2. Create 'submissions' collection ===
if "submissions" not in db.list_collection_names():
    db.create_collection("submissions", validator={
        "$jsonSchema": {
            "bsonType": "object",
            "required": ["form_id", "data", "submitted_at"],
            "properties": {
                "form_id": { "bsonType": "string" },
                "data": { "bsonType": "object" },  # JSON
                "submitted_at": { "bsonType": "date" }
            }
        }
    })
    print("Created 'submissions' collection with validation schema")
else:
    print("'submissions' collection already exists")


"""
alert_recipients:
-- {“user/group/everyone”, “value”,  “sent_times”}
"""
