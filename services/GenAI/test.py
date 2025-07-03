import pytest
import json
from httpx import AsyncClient, ASGITransport
from main import app  # adjust if the file is not named main.py

MOCK_VALID_JSON = json.dumps({
    "title": "Feedback Form",
    "questions": [
        {
            "label": "What did you think about the quality of the workshop materials?",
            "type": "Text",
            "options": None
        },
        {
            "label": "Please provide your email address so we can follow up.",
            "type": "Text",
            "options": None
        },
        {
            "label": "Select the topics you found most useful:",
            "type": "Multiple Choice",
            "options": ["Topic A", "Topic B", "Topic C"]
        },
        {
            "label": "Would you recommend this workshop to others?",
            "type": "Single Choice",
            "options": ["Yes", "No"]
        }
    ]
})

@pytest.mark.anyio("asyncio")
async def test_llm_response_has_valid_format(monkeypatch):
    """Test whether the /generate_form endpoint parses and returns valid structured data"""

    class MockResponse:
        def json(self):
            return {
                "choices": [
                    {
                        "message": {
                            "content": MOCK_VALID_JSON
                        }
                    }
                ]
            }

    def mock_post(*args, **kwargs):
        return MockResponse()

    monkeypatch.setattr("requests.post", mock_post)

    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        response = await client.post("/generate_form", json={"prompt": "A feedback form", "model": ""})
        assert response.status_code == 200
        data = response.json()

        assert "title" in data
        assert isinstance(data["title"], str)
        assert "questions" in data
        assert isinstance(data["questions"], list)
        assert 4 <= len(data["questions"]) <= 8

        for q in data["questions"]:
            assert "label" in q and isinstance(q["label"], str) and len(q["label"]) >= 20
            assert "type" in q and q["type"] in [
                "Text", "Date", "Number", "Multiple Choice",
                "Text Box", "Single Choice", "Dropdown"
            ]
            if q["type"] in ["Multiple Choice", "Single Choice", "Dropdown"]:
                assert isinstance(q["options"], list)
            else:
                assert q["options"] is None
