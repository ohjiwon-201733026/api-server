package com.gloomy.server.domain.notice;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@EqualsAndHashCode
@Getter
@Embeddable
public class IsRead {
    @Column(name = "isRead", nullable = false)
    private Boolean isRead;

    protected IsRead() {
        this.isRead = false;
    }

    public IsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public void read() {
        this.isRead = true;
    }
}
