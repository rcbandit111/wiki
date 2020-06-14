package org.engine.service;

import com.security.oauth.model.User;
import com.security.oauth.repository.UserRepository;
import org.engine.production.service.UsersRepository;
import org.engine.production.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service(value = "userChecksService")
public class UserChecksService implements UserDetailsService {

    private UsersService usersService;

    @Autowired
    public UserChecksService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(username)
                .flatMap(un -> usersService.findByLogin(un))
                .map(u ->  {
                    new AccountStatusUserDetailsChecker().check(u);
                    return u;
                })
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username: %s not found in database", username)));
    }

}
