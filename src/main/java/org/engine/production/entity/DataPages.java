package org.engine.production.entity;

import lombok.*;
import org.engine.utils.LocalDateTimeConverter;
import org.engine.utils.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "data_pages")
public class DataPages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(name = "CONTENT", columnDefinition = "TEXT", length = 65535)
    private String content;

    @Column(length = 255)
    private String type;

    @Column(name = "created_by", length = 4)
    private Integer createdBy;

    @Column(name = "created_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;

    public void update(DataPages newUser) {
        this.updatedAt = LocalDateTime.now();
    }
}
