package com.gloomy.server.domain.common.firebase;

import com.gloomy.server.application.redis.RedisService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
public class FcmService{

    private final RedisService redisService;

    public void sendPushMessage(Long receiverId) throws ExecutionException, InterruptedException {

        if(!redisService.hasKey(String.valueOf(receiverId))) return;

        String token= redisService.getValue(String.valueOf(receiverId));
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
                        .setNotification(new WebpushNotification("title",
                                "message"))
                        .build())
                .build();

        String response=send(message);
        System.out.println("Sent message : "+response);
    }

    public String send(Message message) throws ExecutionException, InterruptedException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    public void saveToken(Long id, String token){
        redisService.setKey(String.valueOf(id),token);
    }

    public void deleteToken(Long id){
        redisService.deleteKey(String.valueOf(id));
    }

    private boolean hasKey(Long id){
        return redisService.hasKey(String.valueOf(id));
    }

    private String getToken(Long id){
        return redisService.getValue(String.valueOf(id));
    }
}
