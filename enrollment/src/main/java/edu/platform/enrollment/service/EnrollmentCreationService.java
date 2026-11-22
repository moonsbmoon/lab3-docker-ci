package edu.platform.enrollment.service;

import edu.platform.enrollment.domain.Enrollment;
import edu.platform.enrollment.domain.IdempotencyRecord;
import edu.platform.enrollment.domain.OutboxEvent;
import edu.platform.enrollment.exception.ConflictException;
import edu.platform.enrollment.repository.EnrollmentRepository;
import edu.platform.enrollment.repository.IdempotencyRepository;
import edu.platform.enrollment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentCreationService {

    private final EnrollmentRepository enrollmentRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public String createEnrollment(Long courseId, Long userId, String idempotencyKey) {
        String requestHash = hash(courseId + ":" + userId);

        Optional<IdempotencyRecord> existing = idempotencyRepository.findById(idempotencyKey);
        if (existing.isPresent()) {
            IdempotencyRecord record = existing.get();
            if (!record.getRequestHash().equals(requestHash)) {
                // ВОТ ЗДЕСЬ ИСПРАВЛЕНО:
                throw new ConflictException("Idempotency-Key already used with different body");
            }
            return record.getResponseBody();
        }

        Enrollment enrollment = new Enrollment(courseId, userId);
        enrollmentRepository.save(enrollment);

        String payload = String.format(
                "{\"eventId\":\"%s\", \"enrollmentId\":%d, \"courseId\":%d, \"userId\":%d}",
                UUID.randomUUID(), enrollment.getId(), courseId, userId
        );
        OutboxEvent outboxEvent = new OutboxEvent(enrollment.getId(), "EnrollmentCreated", payload);
        outboxRepository.save(outboxEvent);

        String response = String.format("{\"enrollmentId\":%d, \"status\":\"ACTIVE\"}", enrollment.getId());
        IdempotencyRecord newRecord = new IdempotencyRecord(idempotencyKey, requestHash, response, 201);
        idempotencyRepository.save(newRecord);

        return response;
    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing", e);
        }
    }
}