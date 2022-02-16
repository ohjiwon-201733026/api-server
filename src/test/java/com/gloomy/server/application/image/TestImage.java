package com.gloomy.server.application.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestImage {
    private static final String testDir = "src/test/resources";
    private static final String testImageFile = "image.jpg";
    private static final String testUpdateImageFile = "updateImage.jpg";

    public static ArrayList<MultipartFile> makeUpdateImages(int imageNum) {
        return makeCommonImages(testUpdateImageFile, imageNum);
    }

    public static ArrayList<MultipartFile> makeImages(int imageNum) {
        return makeCommonImages(testImageFile, imageNum);
    }

    private static ArrayList<MultipartFile> makeCommonImages(String imageFile, int imageNum) {
        ArrayList<MultipartFile> images = new ArrayList<>();
        File fileDir = new File(testDir);
        MultipartFile image = null;
        try {
            image = new MockMultipartFile(testImageFile,
                    new FileInputStream(new File(fileDir.getAbsolutePath() + "/" + imageFile)));
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

    public static MultiValueMap<String, String> convert(Long feedId) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("feedId", feedId.toString());
            return params;
        } catch (Exception e) {
            throw new IllegalArgumentException("[TestImage] 변환 중 오류가 발생했습니다.");
        }
    }
}
