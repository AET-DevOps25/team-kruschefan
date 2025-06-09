-- Create forms table
CREATE TABLE IF NOT EXISTS forms (
    id SERIAL PRIMARY KEY,
    user_id TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    form_data JSONB,
    created_at TIMESTAMPTZ NOT NULL,
    alert_recipients JSONB,
    tags TEXT[]
);

-- Create submissions table
CREATE TABLE IF NOT EXISTS submissions (
    id SERIAL PRIMARY KEY,
    form_id INTEGER NOT NULL REFERENCES forms(id) ON DELETE CASCADE,
    data JSONB,
    submitted_at TIMESTAMPTZ NOT NULL
);
