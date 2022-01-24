package com.gloomy.server.domain.common;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Embeddable
public class CreatedAt {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CreatedAt() {
        createdAt = LocalDateTime.now();
    }

    public CreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
