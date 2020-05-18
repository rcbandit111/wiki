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
@Table(name = "sessions")
public class Sessions implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(name = "session_id", length = 255)
    private String sessionId;

    @Column(length = 255)
    private String data;

    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;
}
