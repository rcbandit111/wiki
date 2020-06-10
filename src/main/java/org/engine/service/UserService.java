package org.engine.service;

import org.engine.exception.EngineException;
import org.engine.exception.ErrorDetail;
import org.engine.production.entity.Users;
import org.engine.production.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class UserService {

    private final UsersService userService;
    private final PasswordAdminResetHandler resetHandler;

    @Autowired
    public UserService(final UsersService userService, final PasswordAdminResetHandler resetHandler) {
        this.userService = userService;
        this.resetHandler = resetHandler;
    }

    public boolean resetRequest(final String login, final String email){
        return userService.findByLogin(login)
                .map(resetUserPassword(email))
                .orElse(false);
    }

    private Function<Users, Boolean> resetUserPassword(String email) {
        return user -> {
            validateEmail(user, email);
            user.setResetPasswordTokenSentAt(LocalDateTime.now());
            userService.save(user);
            resetHandler.sendResetMail(user);
            return true;
        };
    }

    private void validateEmail(final Users users, final String email) {
        if (!users.getEmail().equals(email)) {
            throw new EngineException(ErrorDetail.NOT_FOUND);
        }
    }
}
