CREATE TABLE inbox_events (
                              event_id UUID NOT NULL,
                              handler_id VARCHAR(100) NOT NULL,
                              processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (event_id, handler_id)
);

CREATE TABLE subscriptions (
                               user_id BIGINT PRIMARY KEY,
                               tariff VARCHAR(32) NOT NULL,
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);