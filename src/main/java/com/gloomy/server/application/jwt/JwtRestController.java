package com.gloomy.server.application.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gloomy.server.application.core.response.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtRestController {

    private final JwtService jwtService;

    @PostMapping("/jwt/reissue")
    public JwtDTO.Response reissueToken(@Validated @RequestBody JwtDTO.Request request) {
        return jwtService.reissue(request);
    }

}
