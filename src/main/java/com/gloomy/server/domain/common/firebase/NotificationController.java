package com.gloomy.server.domain.common.firebase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final FcmService fcmService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody String token,@RequestBody Long id){
//        fcmService.saveToken(id,token);
        return ResponseEntity.ok().build();
    }
}
