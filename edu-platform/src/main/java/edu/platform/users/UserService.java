package edu.platform.users;

import edu.platform.shared.ConflictException;
import edu.platform.shared.ResourceNotFoundException;
import edu.platform.shared.UserRegistrationRequest;
import edu.platform.subscriptions.Subscription;
import edu.platform.subscriptions.SubscriptionPlan;
import edu.platform.subscriptions.SubscriptionRepository;
import io.micrometer.core.instrument.MeterRegistry; // 1. Новый импорт
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MeterRegistry meterRegistry; // 2. Внедряем MeterRegistry

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already in use: " + request.email());
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(request.role() != null ? request.role() : Role.STUDENT);
        user = userRepository.save(user);

        Subscription subscription = new Subscription();
        subscription.setUser(user);

        // Переходный период (Expand): пишем в старое и новое поле
        subscription.setTariff(SubscriptionPlan.FREE);

        subscription.setStartedAt(Instant.now());
        subscriptionRepository.save(subscription);

        // 3. Инкрементируем счетчик для БЕСПЛАТНОГО тарифа (Вариант 3)
        meterRegistry.counter("subscription_connections_total", "tariff", "FREE").increment();

        log.info("User registered: email={}, role={}, subscription={}",
                user.getEmail(), user.getRole(), subscription.getTariff());

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return userMapper.toResponse(user);
    }

    // НОВЫЙ МЕТОД, который нужен для SubscriptionService
    @Transactional(readOnly = true)
    public User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}