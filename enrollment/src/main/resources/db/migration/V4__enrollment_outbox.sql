-- 1. Записи на курс
CREATE TABLE enrollments (
                             id BIGSERIAL PRIMARY KEY,
                             course_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Ключи идемпотентности (Задание 4)
CREATE TABLE idempotency_keys (
                                  idempotency_key VARCHAR(255) PRIMARY KEY,
                                  request_hash VARCHAR(255) NOT NULL,
                                  response_body TEXT,
                                  status_code INT
);

-- 3. Таблица Outbox (Задание 11)
CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_id BIGINT NOT NULL,
                               event_type VARCHAR(100) NOT NULL,
                               payload TEXT NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);