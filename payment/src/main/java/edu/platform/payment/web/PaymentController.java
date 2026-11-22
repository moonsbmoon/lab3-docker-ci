package edu.platform.payment.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(
            @RequestParam(required = false, defaultValue = "success") String mode,
            @RequestParam String commandId) {

        System.out.println("Получена команда на оплату: " + commandId + ", режим: " + mode);

        if ("fail".equals(mode)) {
            return ResponseEntity.ok("FAILED");
        }

        if ("delay".equals(mode)) {
            try {
                System.out.println("Задержка оплаты на 5 секунд...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(@RequestParam String commandId) {
        System.out.println("Получена команда на возврат оплаты: " + commandId);
        return ResponseEntity.ok("REFUNDED");
    }
}