package edu.platform.enrollment.domain;

public enum SagaState {
    NEW,            // Создана
    RESERVING,      // Резервирование места
    DEBITING,       // Оплата
    CONFIRMED,      // Запись подтверждена
    COMPENSATING,   // Отмена выполненных действий
    CANCELLED,      // Отмена завершена
    MANUAL_REVIEW   // Требуется ручная проверка
}