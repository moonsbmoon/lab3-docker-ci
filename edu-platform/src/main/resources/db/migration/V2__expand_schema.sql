-- 1. Добавляем цену курса (пункт 5)
ALTER TABLE courses ADD COLUMN price NUMERIC(10, 2) NOT NULL DEFAULT 0.00;

-- 2. Добавляем summary (пункт 10)
ALTER TABLE courses ADD COLUMN summary TEXT;
UPDATE courses SET summary = description; -- Переносим старые данные

-- 3. Добавляем tariff (пункт 16, вариант 3)
ALTER TABLE subscriptions ADD COLUMN tariff VARCHAR(32);
UPDATE subscriptions SET tariff = plan; -- Переносим старые данные

-- 4. Создаем таблицу записей на курс (Enrollment) (пункт 5)
CREATE TABLE enrollments (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL REFERENCES users(id),
                             course_id BIGINT NOT NULL REFERENCES courses(id),
                             status VARCHAR(32) NOT NULL
);

-- 5. Создаем таблицу уроков (Lesson) (пункт 12)
CREATE TABLE lessons (
                         id BIGSERIAL PRIMARY KEY,
                         course_id BIGINT NOT NULL REFERENCES courses(id),
                         title VARCHAR(255) NOT NULL,
                         content TEXT,
                         version BIGINT NOT NULL DEFAULT 0
);

-- 6. Создаем таблицу для файлов (Media) (пункт 13)
CREATE TABLE media_files (
                             id BIGSERIAL PRIMARY KEY,
                             file_name VARCHAR(255) NOT NULL,
                             content_type VARCHAR(100) NOT NULL,
                             size BIGINT NOT NULL,
                             storage_path VARCHAR(512) NOT NULL
);