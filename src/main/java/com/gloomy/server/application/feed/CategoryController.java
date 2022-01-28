package com.gloomy.server.application.feed;

import com.gloomy.server.domain.common.EnumMapper;
import com.gloomy.server.domain.common.EnumValue;
import com.gloomy.server.domain.feed.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/feed/category", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EnumValue> getAllCategory() {
        return EnumMapper.toEnumValues(Category.class);
    }
}
