package org.engine.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.engine.exception.EngineException;
import org.engine.exception.ErrorDetail;
import org.engine.production.entity.Users;
import org.engine.production.service.UsersService;
import org.engine.security.JwtTokenProvider;
import org.engine.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@Component
public class UserRestService {

    private final UsersService userService;
    private final PasswordAdminResetHandler resetHandler;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserRestService(final UsersService userService, final PasswordAdminResetHandler resetHandler,
                           final AuthenticationManager authenticationManager, final JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.resetHandler = resetHandler;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String authorize(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            Optional<Users> user = userService.findByLogin(username);
            if(!user.isPresent()){
                throw new EngineException(ErrorDetail.NOT_FOUND);
            }

            return jwtTokenProvider.createToken(username, Collections.singletonList(user.get().getRole()));
        } catch (AuthenticationException e) {
            throw new EngineException(ErrorDetail.NOT_FOUND);
        }
    }

    public String refresh(String username) {

        Optional<Users> user = userService.findByLogin(username);
        if(!user.isPresent()){
            throw new EngineException(ErrorDetail.NOT_FOUND);
        }

        return jwtTokenProvider.createToken(username, Collections.singletonList(user.get().getRole()));
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
