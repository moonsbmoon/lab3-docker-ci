-- Добавляем колонку для хранения ID события
ALTER TABLE subscriptions ADD COLUMN activation_event_id UUID;

-- Делаем её уникальной, чтобы одно событие не могло активировать подписку дважды
ALTER TABLE subscriptions ADD CONSTRAINT unique_activation_event UNIQUE (activation_event_id);