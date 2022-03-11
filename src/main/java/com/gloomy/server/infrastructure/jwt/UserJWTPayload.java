package com.gloomy.server.infrastructure.jwt;

import com.gloomy.server.domain.jwt.JWTPayload;
import com.gloomy.server.domain.user.User;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.Instant.now;

@Setter
@NoArgsConstructor
public class UserJWTPayload implements JWTPayload {

    private Long sub;
    private long iat;

    static UserJWTPayload of(long epochSecondExpired){
        return new UserJWTPayload(epochSecondExpired);
    }

    static UserJWTPayload of(User user, long epochSecondExpired) {
        return new UserJWTPayload(user.getId(), epochSecondExpired);
    }

    private UserJWTPayload(long iat){
        this.sub=null;
        this.iat=iat;
    }

    // TODO: 파싱이슈
    public UserJWTPayload(long sub, long iat) {
        this.sub = sub;
        this.iat = iat;
    }

    @Override
    public Long getUserId() {
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
        return format("{\"sub\":%d,\"iat\":%d}", sub, iat);
    }
}