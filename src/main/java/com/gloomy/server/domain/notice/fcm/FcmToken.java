package com.gloomy.server.domain.notice.fcm;

import com.gloomy.server.domain.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Getter
@Entity
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    private String fcmToken;

    private FcmToken(User userId, String fcmToken){
        this.userId=userId;
        this.fcmToken=fcmToken;
    }

    public static FcmToken of(User userId,String fcmToken){
        return new FcmToken(userId,fcmToken);
    }

}
