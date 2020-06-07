package org.engine.production.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.engine.utils.LocalDateTimeConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDateTime;


//@Getter // delete
//@Setter // delete
//@NoArgsConstructor // delete
//@AllArgsConstructor // delete
//@Builder(toBuilder = true) // delete
@Data
@Entity
@Table(name = "users")
public class Users implements Serializable {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    //    permit save two user with one login? why not.
    @Column(length = 255)
    private String login;

    @Column(length = 255, unique = true)
    @Email
    private String email;

    @Column(name = "encrypted_password", length = 255)
    private String encryptedPassword;

    @Column(name = "password_changed_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime passwordChangedAt;

    @Column(length = 64)
    private String salt;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "failed_attempts", length = 4)
    private Integer failedAttempts;

    //   what this mean? who owner? slave owner?
    @Column(name = "owner_id", length = 4)
    private Integer ownerId;

    @Column(name = "owner_type", length = 255)
    private String ownerType;

    //    roles move to new entity
    @Column(length = 25)
    private String role;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(length = 255)
    private String type;

    //    all data with Local.Date like create_at and etc. move new entity
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

    @Column(name = "password")
    @JsonIgnore
    private String password;

    public Users() {
        super();
        this.enabled=false;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    //    TODO ? updating process by repository not entity
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
}
