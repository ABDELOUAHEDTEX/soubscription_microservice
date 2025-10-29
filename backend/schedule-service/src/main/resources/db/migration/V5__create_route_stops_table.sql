CREATE TABLE IF NOT EXISTS route_stops (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL,
    stop_id BIGINT NOT NULL,
    sequence_number INTEGER NOT NULL,
    distance_from_origin DECIMAL(10,2) NOT NULL DEFAULT 0.0,
    time_from_origin INTEGER NOT NULL DEFAULT 0,
    dwell_time INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    FOREIGN KEY (stop_id) REFERENCES stops(id) ON DELETE CASCADE,
    UNIQUE (route_id, stop_id),
    UNIQUE (route_id, sequence_number)
);

CREATE INDEX idx_route_stops_route ON route_stops(route_id);
CREATE INDEX idx_route_stops_stop ON route_stops(stop_id);
CREATE INDEX idx_route_stops_sequence ON route_stops(route_id, sequence_number);