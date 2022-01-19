package com.gloomy.server.application.image;

import com.gloomy.server.domain.image.IMAGE_STATUS;
import com.gloomy.server.domain.image.Image;
import com.gloomy.server.domain.image.UserProfileImage;
import com.gloomy.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileImageRepository extends JpaRepository<UserProfileImage, Long> {
    UserProfileImage findAllByUserIdAndStatus(User userId, IMAGE_STATUS status);
}
