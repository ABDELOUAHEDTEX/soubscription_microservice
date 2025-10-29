CREATE TABLE IF NOT EXISTS stops (
    id BIGSERIAL PRIMARY KEY,
    stop_code VARCHAR(50) NOT NULL UNIQUE,
    stop_name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    address VARCHAR(500),
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    city VARCHAR(100),
    district VARCHAR(100),
    postal_code VARCHAR(20),
    has_waiting_shelter BOOLEAN NOT NULL DEFAULT FALSE,
    has_seating BOOLEAN NOT NULL DEFAULT FALSE,
    is_accessible BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_stops_code ON stops(stop_code);
CREATE INDEX idx_stops_location ON stops(latitude, longitude);
CREATE INDEX idx_stops_active ON stops(is_active);
CREATE INDEX idx_stops_city ON stops(city);