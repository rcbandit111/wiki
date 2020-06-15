package org.engine.production.entity;

import lombok.*;
import org.engine.utils.LocalDateTimeConverter;
import org.engine.utils.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
public class Users implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    @Column(length = 255)
    private String login;

    @Column(length = 255)
    private String email;

    @Column(name = "encrypted_password", length = 255)
    private String encryptedPassword;

    @Column(name = "password_changed_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime passwordChangedAt;

    @Column(length = 64)
    private String salt;

    @Column(columnDefinition = "TINYINT", length = 1)
    private Boolean enabled;

    @Column(name = "failed_attempts", length = 4)
    private Integer failedAttempts;

    @Column(name = "owner_id", length = 4)
    private Integer ownerId;

    @Column(name = "owner_type", length = 255)
    private String ownerType;

    @Column(length = 25)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(length = 255)
    private String type;

    @Column(name = "client_cert", length = 512)
    private String clientCert;

    @Column(name = "created_by", length = 4)
    private Integer createdBy;

    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;

    @Column(name = "ENTITY_PRIVILEGES", columnDefinition = "TEXT", length = 65535)
    private String entityPrivileges;

    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    @Column(name = "reset_password_token_sent_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime resetPasswordTokenSentAt;

    @Column(name = "reset_password_sent_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime resetPasswordSentAt;

    @Column(name = "confirmation_token", length = 255)
    private String confirmationToken;

    @Column(name = "confirmed_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime confirmedAt;

    @Column(name = "confirmation_sent_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime confirmationSentAt;

    @Column(name = "locked_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lockedAt;

    @Column(name = "expired_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime expiredAt;

    public void update(Users newUser) {
        this.login = newUser.getLogin();
        this.email = newUser.getEmail();
        this.firstName = newUser.getFirstName();
        this.lastName = newUser.getLastName();
        this.role = newUser.getRole();
        this.enabled = newUser.getEnabled();
        this.updatedAt = LocalDateTime.now();
        this.ownerId = newUser.getOwnerId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
