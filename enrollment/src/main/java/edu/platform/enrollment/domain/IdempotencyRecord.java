package edu.platform.enrollment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "idempotency_keys")
@Getter @Setter @NoArgsConstructor
public class IdempotencyRecord {
    @Id
    private String idempotencyKey;

    private String requestHash;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private Integer statusCode;

    public IdempotencyRecord(String idempotencyKey, String requestHash, String responseBody, Integer statusCode) {
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }
}