package com.gloomy.server.application.feed.sort;

import com.gloomy.server.domain.common.EnumMapper;
import com.gloomy.server.domain.common.EnumValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
class FeedSortTest {
    @Test
    void 피드_정렬기준_리스트_조회() {
        List<EnumValue> sortValues = EnumMapper.toEnumValues(FeedSort.class);
        checkSort(sortValues.get(0), "DATE", "최신순");
        checkSort(sortValues.get(1), "LIKE", "인기순");
    }

    private void checkSort(EnumValue sort, String code, String title) {
        assertEquals(sort.getCode(), code);
        assertEquals(sort.getTitle(), title);
    }
}
