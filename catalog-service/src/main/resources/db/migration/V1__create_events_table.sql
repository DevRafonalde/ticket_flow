CREATE TABLE catalog.events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    venue VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    total_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
