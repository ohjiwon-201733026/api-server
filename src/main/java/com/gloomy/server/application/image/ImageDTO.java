package com.gloomy.server.application.image;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.*;

public class ImageDTO {
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        @NotNull
        private Long feedId;
        private List<MultipartFile> images;

        public Request(Long feedId, List<MultipartFile> images) {
            this.feedId = feedId;
            this.images = images;
        }
    }

    @Getter
    public static class Response {
        private final Long feedId;
        private final List<Map<String, Object>> images;

        @Builder
        public Response(Long feedId, Images images) {
            this.feedId = feedId;
            this.images = toDTO(images);
        }

        public static ImageDTO.Response of(Images images) {
            if (images == null) {
                return null;
            }
            return builder()
                    .feedId(images.getImages().get(0).getFeedId().getId())
                    .images(images)
                    .build();
        }

        public List<Map<String, Object>> toDTO(Images images) {
            List<Map<String, Object>> toImageDTO = new ArrayList<>();
            images.getImages().forEach(image -> {
                Map<String, Object> toDTO = new LinkedHashMap<>();
                toDTO.put("id", image.getId());
                toDTO.put("imageURL", image.getImageUrl().getImageUrl());
                toDTO.put("status", image.getStatus().getStatusName());
                toDTO.put("createdAt", image.getCreatedAt().getCreatedAt());
                toDTO.put("updatedAt", image.getUpdatedAt().getUpdatedAt());
                toDTO.put("deletedAt", image.getDeletedAt().getDeletedAt());
                toImageDTO.add(toDTO);
            });
            return toImageDTO;
        }
    }
}
