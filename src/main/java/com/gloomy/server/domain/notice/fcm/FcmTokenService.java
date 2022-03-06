package com.gloomy.server.domain.notice.fcm;

import com.gloomy.server.application.notice.fcm.FcmDto;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserRepository;
import com.gloomy.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional
public class FcmTokenService {

    private final FcmRepository fcmRepository;
    private final UserService userService;

    public FcmToken saveFcmToken(FcmDto.Request fcmTokenDto){
        User user=userService.findUser(fcmTokenDto.getUserId());
        FcmToken fcmToken=FcmToken.of(user,fcmTokenDto.getFcmToken());
        return fcmRepository.save(fcmToken);
    }

}
