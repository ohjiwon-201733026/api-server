package com.gloomy.server.domain.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class DeletedAt {
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public DeletedAt() {
    }

    public DeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
