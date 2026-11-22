package edu.platform.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // REQUIRES_NEW: приостанавливает текущую транзакцию и открывает новую
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyCourseCreated(Long courseId, String teacherEmail) {
        log.info("Notification: course {} created by {}", courseId, teacherEmail);
    }
}