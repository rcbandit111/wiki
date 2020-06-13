package org.engine.security;

import org.engine.production.entity.Users;
import org.engine.production.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsHandler implements UserDetailsService {

    @Autowired
    private UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<Users> user = usersService.findByLogin(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

  //    return org.springframework.security.core.userdetails.User
  //        .withUsername(username)
  //        .password(user.get().getEncryptedPassword())
  //        .authorities(user.get().getRole())
  //
  //        // TODO... Implement checks here
  //        .accountExpired(false)
  //        .accountLocked(false)
  //        .credentialsExpired(false)
  //        .disabled(false)
  //        .build();

        return user
            .map(value -> {
                return new User(
                        value.getLogin(),
                        value.getEncryptedPassword(),
                        value.getEnabled(),
                        hasAccountExpired(value.getExpiredAt()),
                        hasPasswordExpired(value.getPasswordChangedAt()),
                        hasAccountLocked(value.getLockedAt()),
                        Collections.singleton(new SimpleGrantedAuthority(value.getRole().getAuthority()))
                );
            }).orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
    }

    /**
     * Check if account is expired
     * @param account_expired_at
     * @return
     */
    private boolean hasAccountExpired(LocalDateTime account_expired_at) {

        return account_expired_at == null;
    }

    /**
     * Passwords should not be older than 45 days
     * @param password_changed_at
     * @return
     */
    private boolean hasPasswordExpired(LocalDateTime password_changed_at) {

        if(password_changed_at == null) {
            return false;
        }

        return Duration.between(password_changed_at, LocalDateTime.now()).toDays() <= 45;
    }

    /**
     * Check if account is locked
     * @param account_locked_at
     * @return
     */
    private boolean hasAccountLocked(LocalDateTime account_locked_at) {

        return account_locked_at == null;
    }
}
