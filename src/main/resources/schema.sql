CREATE TABLE IF NOT EXISTS call_history (
    id SERIAL PRIMARY KEY,
    endpoint VARCHAR(255),
    parameters TEXT,
    response TEXT,
    created_at TIMESTAMP DEFAULT now()
);
