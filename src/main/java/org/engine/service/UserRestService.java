package org.engine.service;

import org.engine.dto.AuthenticationTokenDTO;
import org.engine.exception.EngineException;
import org.engine.exception.ErrorDetail;
import org.engine.production.entity.Users;
import org.engine.production.service.UsersService;
import org.engine.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
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

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public AuthenticationTokenDTO authorize(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            Optional<Users> user = userService.findByLogin(username);
            if(!user.isPresent()){
                throw new EngineException(ErrorDetail.NOT_FOUND);
            }

            return jwtTokenProvider.createToken(username, Collections.singletonList(user.get().getRole()));
        } catch (BadCredentialsException ex){
            throw new EngineException(ErrorDetail.BAD_CREDENTIALS);
        } catch (CredentialsExpiredException ex){
            throw new EngineException(ErrorDetail.EXPIRED_CREDENTIALS);
        } catch (DisabledException ex) {
            throw new EngineException(ErrorDetail.DISABLED_USER);
        } catch (AuthenticationException ex) {
            throw new EngineException(ErrorDetail.AUTHENTICATION_ERROR);
        }
    }

    /**
     *
     * @param username
     * @return
     */
    public AuthenticationTokenDTO refresh(String username) {

        Optional<Users> user = userService.findByLogin(username);
        if(!user.isPresent()){
            throw new EngineException(ErrorDetail.NOT_FOUND);
        }

        return jwtTokenProvider.createToken(username, Collections.singletonList(user.get().getRole()));
    }

    /**
     *
     * @param login
     * @param email
     * @return
     */
    public boolean resetRequest(final String login, final String email){
        return userService.findByLogin(login)
                .map(resetUserPassword(email))
                .orElse(false);
    }

    /**
     *
     * @param email
     * @return
     */
    private Function<Users, Boolean> resetUserPassword(String email) {
        return user -> {
            validateEmail(user, email);
            user.setResetPasswordTokenSentAt(LocalDateTime.now());
            userService.save(user);
            resetHandler.sendResetMail(user);
            return true;
        };
    }

    /**
     *
     * @param users
     * @param email
     */
    private void validateEmail(final Users users, final String email) {
        if (!users.getEmail().equals(email)) {
            throw new EngineException(ErrorDetail.NOT_FOUND);
        }
    }
}
