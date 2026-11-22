package edu.platform.subscriptions;

import edu.platform.shared.ResourceNotFoundException;
import edu.platform.users.User;
import edu.platform.users.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               MeterRegistry meterRegistry) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getForUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Subscription sub = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        // ИСПРАВЛЕНИЕ: передаем 4 аргумента (id, tariff.name(), startedAt, expiresAt)
        return new SubscriptionResponse(
                sub.getId(),
                sub.getTariff().name(),
                sub.getStartedAt(),
                sub.getExpiresAt()
        );
    }

    @Transactional
    public SubscriptionResponse upgradeToPremium(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Subscription sub = subscriptionRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        sub.setTariff(SubscriptionPlan.PREMIUM);
        sub.setExpiresAt(Instant.now().plusSeconds(30L * 24 * 60 * 60)); // 30 дней
        subscriptionRepository.save(sub);

        // Метрика варианта 3
        meterRegistry.counter("platform_subscriptions_upgrades_total", "tariff", "PREMIUM").increment();

        // ИСПРАВЛЕНИЕ: передаем 4 аргумента (id, tariff.name(), startedAt, expiresAt)
        return new SubscriptionResponse(
                sub.getId(),
                sub.getTariff().name(),
                sub.getStartedAt(),
                sub.getExpiresAt()
        );
    }
}