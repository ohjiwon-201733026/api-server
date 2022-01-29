package com.gloomy.server.application.feed.sort;

import com.gloomy.server.domain.common.EnumMapper;
import com.gloomy.server.domain.common.EnumValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/feed/sort", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeedSortRestController {
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EnumValue> getAllCategory() {
        return EnumMapper.toEnumValues(FeedSort.class);
    }
}
