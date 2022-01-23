package com.gloomy.server.domain.image;

import com.gloomy.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
public class UserProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Embedded
    private ImageURL imageUrl;

    @Column(name = "status", nullable = false)
    private ImageStatus status;

    private UserProfileImage() {
    }

    @Builder(builderClassName = "userImageBuilder", builderMethodName = "userImageBuilder")
    private UserProfileImage(User userId, ImageURL imageUrl, ImageStatus status) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public static UserProfileImage of(User userId, String imageUrl) {
        return userImageBuilder()
                .userId(userId)
                .imageUrl(new ImageURL(imageUrl))
                .status(ImageStatus.ACTIVE)
                .build();
    }

    public void setStatus(ImageStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserProfileImage) {
            UserProfileImage targetImage = (UserProfileImage) o;
            return Objects.equals(id, targetImage.id)
                    && Objects.equals(userId.getId(), targetImage.userId.getId())
                    && Objects.equals(imageUrl.getImageUrl(), targetImage.imageUrl.getImageUrl());
        }
        return false;
    }

}
