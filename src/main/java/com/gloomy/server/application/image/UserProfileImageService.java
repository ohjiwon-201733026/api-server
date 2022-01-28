package com.gloomy.server.application.image;

import com.gloomy.server.application.image.s3.S3Uploader;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.image.UserProfileImage;
import com.gloomy.server.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserProfileImageService {
    private final S3Uploader s3Uploader;
    private final UserProfileImageRepository userProfileImageRepository;
    private final String defaultImage = "https://gl00my-bucket.s3.ap-northeast-2.amazonaws.com/user/default/bc1e908a-e345-4941-8445-0a9bf0849a49s3_tmp.jpg";

    public UserProfileImageService(S3Uploader s3Uploader, UserProfileImageRepository userProfileImageRepository) {
        this.s3Uploader = s3Uploader;
        this.userProfileImageRepository = userProfileImageRepository;
    }

    public UserProfileImage findImageByUserId(User userId) {
        UserProfileImage image = userProfileImageRepository.findAllByUserIdAndStatus(userId, Status.ACTIVE);
        if (image == null) return UserProfileImage.of(userId, defaultImage);
        return image;
    }

    public UserProfileImage uploadUserImage(User user, MultipartFile multipartFile) {
        validateUserAndImage(user, multipartFile);
        UserProfileImage foundImage = userProfileImageRepository.findAllByUserIdAndStatus(user, Status.ACTIVE);
        if (foundImage != null) foundImage.setStatus(Status.INACTIVE);

        String dirName = "user/" + String.valueOf(user.getId());
        String uploadImageUrl = s3Uploader.upload(dirName, multipartFile);
        UserProfileImage image = UserProfileImage.of(user, uploadImageUrl);

        return userProfileImageRepository.save(image);
    }

    private void validateUserAndImage(User user, MultipartFile multipartFile) {
        if (user == null)
            throw new IllegalArgumentException("[ UserProfileImageService ] 유효하지 않은 사용자입니다.");
        if (multipartFile == null)
            throw new IllegalArgumentException("[ UserProfileImageService ] 유효하지 않은 이미지입니다.");
    }

    public void deleteAll(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("[ UserProfileImageService ] 유효하지 않은 사용자입니다.");
        }
        String userId = String.valueOf(user.getId());
        s3Uploader.deleteDir("user/" + userId + "/");
        userProfileImageRepository.deleteAll();
    }

    // ?
    private <T> void checkValidate(T check) {
        if (check == null) {
            throw new IllegalArgumentException("[ UserProfileImageService ] 유효하지 않은" + check.getClass() + "입니다.");
        }
    }


}
