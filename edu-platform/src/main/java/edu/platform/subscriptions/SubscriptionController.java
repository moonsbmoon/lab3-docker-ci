package edu.platform.subscriptions;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    public SubscriptionResponse getMe(Authentication authentication) {
        return subscriptionService.getForUserEmail(authentication.getName());
    }

    @PostMapping("/upgrade")
    public SubscriptionResponse upgrade(Authentication authentication) {
        return subscriptionService.upgradeToPremium(authentication.getName());
    }
}