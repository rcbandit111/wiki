package org.engine.production.entity;

import lombok.*;
import org.engine.utils.LocalDateTimeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "old_passwords")
public class OldPasswords implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(name = "encrypted_password", length = 255)
    private String encryptedPassword;

    @Column(name = "password_owner_type", length = 255)
    private String passwordOwnerType;

    @Column(name = "password_owner_id", length = 4)
    private Integer passwordOwnerId;

    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
}
