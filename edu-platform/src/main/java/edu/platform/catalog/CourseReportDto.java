package edu.platform.catalog;

public interface CourseReportDto {
    Long getCourseId();
    String getTitle();
    Long getActiveCount();
    Long getPremiumCount(); // Вариант 3
}