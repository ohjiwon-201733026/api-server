package com.gloomy.server.application.image;

import com.gloomy.server.application.image.s3.S3Uploader;
import com.gloomy.server.domain.image.Image;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Service
public class ImageService {
    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;

    public ImageService(S3Uploader s3Uploader, ImageRepository imageRepository) {
        this.s3Uploader = s3Uploader;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public Images uploadMany(ArrayList<MultipartFile> multipartFiles) throws IllegalArgumentException {
        validateImages(multipartFiles);
        Images images = new Images();
        if (!ObjectUtils.isEmpty(multipartFiles)) {
            for (MultipartFile multipartFile : multipartFiles) {
                Image createdImage = uploadOne(multipartFile);
                images.addImage(createdImage);
            }
        }
        return images;
    }

    private void validateImages(ArrayList<MultipartFile> multipartFiles) throws IllegalArgumentException {
        if (multipartFiles == null) {
            throw new IllegalArgumentException("[ImageService] 이미지 파일이 존재하지 않습니다.");
        }
    }

    private Image uploadOne(MultipartFile multipartFile) {
        final String dirName = "/";
        String uploadImageUrl = s3Uploader.upload(dirName, multipartFile);
        Image image = Image.of(uploadImageUrl);
        return imageRepository.save(image);
    }

    public void deleteAll() {
        imageRepository.deleteAll();
        s3Uploader.deleteAll();
    }
}
