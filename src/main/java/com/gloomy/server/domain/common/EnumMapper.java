package com.gloomy.server.domain.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumMapper {
    public static List<EnumValue> toEnumValues(Class<? extends EnumModel> e) {
        return Arrays
                .stream(e.getEnumConstants())
                .map(EnumValue::new)
                .collect(Collectors.toList());
    }
}
