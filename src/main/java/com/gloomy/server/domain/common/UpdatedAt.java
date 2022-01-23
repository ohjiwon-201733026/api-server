package com.gloomy.server.domain.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class UpdatedAt {
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UpdatedAt() {
    }

    public UpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
