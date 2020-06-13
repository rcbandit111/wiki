package org.engine.service;

import org.engine.production.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class DbUserDetailsService implements UserDetailsService {

    private UsersService userRepository;

    @Autowired
    public DbUserDetailsService(UsersService userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(user -> {
                    return new User(
                            user.getLogin(),
                            user.getEncryptedPassword(),
                            user.getEnabled(),
                            hasAccountExpired(user.getExpiredAt()),
                            hasPasswordExpired(user.getPasswordChangedAt()),
                            hasAccountLocked(user.getLockedAt()),
                            Collections.singleton(new SimpleGrantedAuthority(user.getRole().getAuthority()))
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
