package com.gloomy.server.application.image;

import com.gloomy.server.application.image.s3.S3Uploader;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.feed.Feed;
import com.gloomy.server.domain.image.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImageService {
    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.feedDir}")
    private String feedDir;

    public ImageService(S3Uploader s3Uploader, ImageRepository imageRepository) {
        this.s3Uploader = s3Uploader;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public Images uploadImages(Feed feedId, List<MultipartFile> multipartFiles) throws IllegalArgumentException {
        Images images = null;
        if (multipartFiles != null) {
            images = new Images();
            validateFeedId(feedId);
            for (MultipartFile multipartFile : multipartFiles) {
                Image createdImage = uploadOne(feedId, multipartFile);
                images.addImage(createdImage);
            }
        }
        return images;
    }

    private Image uploadOne(Feed feed, MultipartFile multipartFile) {
        String dirName = feedDir + feed.getId();
        String uploadImageUrl = s3Uploader.upload(dirName, multipartFile);
        Image image = Image.of(feed, uploadImageUrl);
        return imageRepository.save(image);
    }

    @Transactional(readOnly = true)
    public Images findAllImages(Feed feedId) throws IllegalArgumentException {
        validateFeedId(feedId);
        return new Images(imageRepository.findAllByFeedId(feedId));
    }

    @Transactional(readOnly = true)
    public Images findAllActiveImages(Feed feedId) throws IllegalArgumentException {
        validateFeedId(feedId);
        return new Images(imageRepository.findAllByFeedIdAndStatus(feedId, Status.active()));
    }

    @Transactional(readOnly = true)
    public Image findOneImage(Long imageId) {
        validateImageId(imageId);
        return imageRepository.findById(imageId).orElseThrow(() -> {
            throw new IllegalArgumentException("[ImageService] 해당 이미지 ID가 존재하지 않습니다.");
        });
    }

    @Transactional
    public Images updateImages(Feed feedId, List<MultipartFile> images) {
        validateFeedId(feedId);
        deleteImages(feedId);
        return uploadImages(feedId, images);
    }

    @Transactional
    public void deleteImages(Feed feedId) throws IllegalArgumentException {
        validateFeedId(feedId);
        imageRepository.deleteAllByFeedId(feedId);
    }

    @Transactional
    public void deleteImage(Long imageId) throws IllegalArgumentException {
        validateImageId(imageId);
        Image foundImage = findOneImage(imageId);
        imageRepository.delete(foundImage);
    }

    @Transactional
    public void deleteAll(String dir) {
        imageRepository.deleteAll();
        s3Uploader.deleteDir(dir);
    }

    private void validateFeedId(Feed feedId) {
        if (feedId == null) {
            throw new IllegalArgumentException("[ImageService] 해당 피드가 유효하지 않습니다.");
        }
    }

    private void validateImageId(Long imageId) {
        if (imageId == null || imageId <= 0) {
            throw new IllegalArgumentException("[ImageService] 해당 이미지 ID가 유효하지 않습니다.");
        }
    }
}
