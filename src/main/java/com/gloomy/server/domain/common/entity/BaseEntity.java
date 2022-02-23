package com.gloomy.server.domain.common.entity;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    protected Status status;

    @Embedded
    protected CreatedAt createdAt;

    @Embedded
    protected UpdatedAt updatedAt;

    @Embedded
    protected DeletedAt deletedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = new CreatedAt(now);
        updatedAt = new UpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt.setUpdatedAt(LocalDateTime.now());
    }
}
