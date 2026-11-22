package edu.platform.subscriptions;

import edu.platform.subscriptions.Subscription;
import edu.platform.subscriptions.SubscriptionPlan;
import edu.platform.subscriptions.SubscriptionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    // MapStruct сам подхватит поле tariff из сущности
    SubscriptionResponse toResponse(Subscription subscription);

    default String map(SubscriptionPlan plan) {
        return plan == null ? null : plan.name();
    }
}