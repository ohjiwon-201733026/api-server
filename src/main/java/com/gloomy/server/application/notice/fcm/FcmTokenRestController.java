package com.gloomy.server.application.notice.fcm;

import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.common.firebase.FcmService;
import com.gloomy.server.domain.notice.fcm.FcmToken;
import com.gloomy.server.domain.notice.fcm.FcmTokenService;
import com.gloomy.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Transactional
@RequestMapping("/fcm")
public class FcmTokenRestController{

    private final FcmTokenService fcmTokenService;

    @PostMapping(value = "/save")
    public void saveFcmToken(@Validated @RequestBody FcmDto.Request request) {
        fcmTokenService.saveFcmToken(request);
    }
}
