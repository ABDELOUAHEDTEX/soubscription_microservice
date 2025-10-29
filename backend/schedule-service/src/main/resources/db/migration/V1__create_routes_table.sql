CREATE TABLE IF NOT EXISTS routes (
    id BIGSERIAL PRIMARY KEY,
    route_number VARCHAR(50) NOT NULL UNIQUE,
    route_name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    origin VARCHAR(200) NOT NULL,
    destination VARCHAR(200) NOT NULL,
    distance DECIMAL(10,2) NOT NULL,
    estimated_duration INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_circular BOOLEAN NOT NULL DEFAULT FALSE,
    color VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_routes_number ON routes(route_number);
CREATE INDEX idx_routes_active ON routes(is_active);
CREATE INDEX idx_routes_origin_destination ON routes(origin, destination);