package com.gloomy.server.application.feed.category;

import com.gloomy.server.domain.common.EnumMapper;
import com.gloomy.server.domain.common.EnumValue;
import com.gloomy.server.domain.feed.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class CategoryTest {
    @Test
    void 카테고리_리스트_조회() {
        List<EnumValue> categoryValues = EnumMapper.toEnumValues(Category.class);

        checkCategory(categoryValues.get(0), "ALL", "모든 고민");
        checkCategory(categoryValues.get(1), "CHAT", "잡담");
        checkCategory(categoryValues.get(2), "FAMILY", "가족");
        checkCategory(categoryValues.get(3), "FRIEND", "친구");
        checkCategory(categoryValues.get(4), "COMPANY", "직장/이직");
        checkCategory(categoryValues.get(5), "EMPLOY", "취업");
        checkCategory(categoryValues.get(6), "STUDY", "학업/시험");
        checkCategory(categoryValues.get(7), "HETEROLOVE", "연애(이성애)");
        checkCategory(categoryValues.get(8), "HOMOLOVE", "연애(동성애)");
        checkCategory(categoryValues.get(9), "UNDEFINED", "미정");
    }

    private void checkCategory(EnumValue category, String code, String title) {
        assertEquals(category.getCode(), code);
        assertEquals(category.getTitle(), title);
    }
}
