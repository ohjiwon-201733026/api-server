package com.gloomy.server.domain.notice.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class FcmTokenService {

    private final FcmRepository fcmRepository;

    public FcmToken saveFcmToken(FcmToken fcmToken){
        return fcmRepository.save(fcmToken);
    }

}
