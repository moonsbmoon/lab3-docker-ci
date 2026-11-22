package edu.platform.users;

import edu.platform.subscriptions.SubscriptionMapper;
import edu.platform.users.Role;
import edu.platform.users.User;
import edu.platform.users.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SubscriptionMapper.class)
public interface UserMapper {

    UserResponse toResponse(User user);

    default String map(Role role) {
        return role == null ? null : role.name();
    }
}