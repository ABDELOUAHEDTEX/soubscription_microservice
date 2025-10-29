CREATE TABLE IF NOT EXISTS buses (
    id BIGSERIAL PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL UNIQUE,
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    manufacturer VARCHAR(100),
    model VARCHAR(100),
    year INTEGER,
    capacity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    fuel_type VARCHAR(50),
    has_air_conditioning BOOLEAN NOT NULL DEFAULT FALSE,
    has_wifi BOOLEAN NOT NULL DEFAULT FALSE,
    is_accessible BOOLEAN NOT NULL DEFAULT FALSE,
    has_gps BOOLEAN NOT NULL DEFAULT TRUE,
    last_maintenance_date DATE,
    next_maintenance_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_buses_number ON buses(bus_number);
CREATE INDEX idx_buses_registration ON buses(registration_number);
CREATE INDEX idx_buses_status ON buses(status);