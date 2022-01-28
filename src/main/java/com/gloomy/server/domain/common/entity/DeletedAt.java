package com.gloomy.server.domain.common.entity;

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
        deletedAt = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    }

    public DeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
