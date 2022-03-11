package com.gloomy.server.application.report;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ReportDTO {

    @NoArgsConstructor
    @Getter
    public static class Request{
        @NotEmpty
        private Long feedId;
        @NotEmpty
        private String reportCategory;

        public Request(Long feedId, String reportCategory) {
            this.feedId = feedId;
            this.reportCategory = reportCategory;
        }

        public static Request of(Long feedId, String reportCategory){
            return new Request(feedId,reportCategory);
        }

    }

    static class Response{
        private Long reportId;
        private Long feedId;
        private Long userId;
        private String reportCategory;
    }
}