CREATE TABLE users (
                       id        BIGSERIAL PRIMARY KEY,
                       email     VARCHAR(255) NOT NULL UNIQUE,
                       password  VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       role      VARCHAR(32)  NOT NULL
);

CREATE TABLE courses (
                         id          BIGSERIAL PRIMARY KEY,
                         title       VARCHAR(255) NOT NULL,
                         description VARCHAR(2000),
                         teacher_id  BIGINT NOT NULL REFERENCES users(id),
                         version     BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE subscriptions (
                               id         BIGSERIAL PRIMARY KEY,
                               user_id    BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                               plan       VARCHAR(32) NOT NULL,
                               started_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               expires_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_courses_teacher_id ON courses(teacher_id);
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
