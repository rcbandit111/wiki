package org.engine.usermanagement.service;

import org.engine.exception.ValidationException;
import org.engine.service.PasswordAdminResetHandler;
import org.engine.usermanagement.model.Users;
import org.engine.usermanagement.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;


@Component
public class UserService {

    private final UsersRepository userService;
    private final PasswordAdminResetHandler resetHandler;

    @Autowired
    public UserService(final UsersRepository userService, final PasswordAdminResetHandler resetHandler) {
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
            throw new ValidationException("NAME_AND_EMAIL_MISMATCH");
        }
    }
}
