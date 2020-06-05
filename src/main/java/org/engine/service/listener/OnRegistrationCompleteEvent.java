package org.engine.service.listener;

import org.engine.production.entity.Users;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private Users user;

    public OnRegistrationCompleteEvent(Users user) {
        super(user);
        this.user = user;
    }

    public Users getUser() {
        return user;
    }
}
