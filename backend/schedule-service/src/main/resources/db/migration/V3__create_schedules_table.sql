CREATE TABLE IF NOT EXISTS schedules (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL,
    bus_id BIGINT,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    schedule_type VARCHAR(50) NOT NULL,
    operating_days VARCHAR(255),
    effective_from DATE NOT NULL,
    effective_until DATE,
    frequency INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);

CREATE INDEX idx_schedules_route ON schedules(route_id);
CREATE INDEX idx_schedules_bus ON schedules(bus_id);
CREATE INDEX idx_schedules_time ON schedules(departure_time, arrival_time);
CREATE INDEX idx_schedules_active ON schedules(is_active);
CREATE INDEX idx_schedules_effective_dates ON schedules(effective_from, effective_until);