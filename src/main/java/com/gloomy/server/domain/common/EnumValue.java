package com.gloomy.server.domain.common;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EnumValue {
    private final String code;
    private final String title;

    public EnumValue(EnumModel enumModel) {
        this.code = enumModel.getCode();
        this.title = enumModel.getTitle();
    }
}
