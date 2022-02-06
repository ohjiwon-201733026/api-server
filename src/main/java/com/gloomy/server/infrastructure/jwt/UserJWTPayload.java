package com.gloomy.server.infrastructure.jwt;

import com.gloomy.server.domain.jwt.JWTPayload;
import com.gloomy.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.Instant.now;

@Setter
@NoArgsConstructor
public class UserJWTPayload implements JWTPayload {

    private long sub;
    private String name;
    private long iat;

    static UserJWTPayload of(User user, long epochSecondExpired) {
        return new UserJWTPayload(user.getId(), valueOf(user.getEmail()), epochSecondExpired);
    }

    // TODO: 파싱이슈
    public UserJWTPayload(long sub, String name, long iat) {
        this.sub = sub;
        this.name = name;
        this.iat = iat;
    }

    @Override
    public long getUserId() {
        return sub;
    }

    @Override
    public boolean isExpired() {
        return iat < now().getEpochSecond();
    }

    @Override
    public long getExpiredTime() {
        return this.iat;
    }

    @Override
    public String toString() {
        return format("{\"sub\":%d,\"name\":\"%s\",\"iat\":%d}", sub, name, iat);
    }
}