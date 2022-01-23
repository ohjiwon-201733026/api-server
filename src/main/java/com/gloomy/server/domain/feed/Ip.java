package com.gloomy.server.domain.feed;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
public class Ip {
    @Column(name = "ip", nullable = false)
    private String ip;

    protected Ip() {
    }

    public Ip(String ip) {
        this.ip = ip;
    }
}
