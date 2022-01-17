package com.gloomy.server.application.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestImage {
    private final String testDir = "src/test/resources";
    private final String testFile = "test.jpg";

    public ArrayList<MultipartFile> makeImages(int imageNum) {
        ArrayList<MultipartFile> images = new ArrayList<>();
        File fileDir = new File(testDir);
        MultipartFile image = null;
        try {
            image = new MockMultipartFile(testFile,
                    new FileInputStream(new File(fileDir.getAbsolutePath() + "/" + testFile)));
        } catch (IOException e) {
            log.info("[FeedServiceTest] 테스트 파일 이미지 변환 실패했습니다.");
            e.printStackTrace();
        }

        for (int i = 0; i < imageNum; i++) {
            images.add(image);
        }
        return images;
    }

    public static MockMultipartFile convert(List<MultipartFile> images, int imageNum) {
        try {
            return new MockMultipartFile("images",
                    images.get(imageNum).getOriginalFilename(),
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    images.get(imageNum).getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException("[TestImage] 이미지 파일 변환 중 오류가 발생했습니다.");
        }
    }

    public static MockMultipartFile convertOne(MultipartFile image) {
        try {
            return new MockMultipartFile("image",
                    image.getOriginalFilename(),
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    image.getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException("[TestImage] 이미지 파일 변환 중 오류가 발생했습니다.");
        }
    }
}
