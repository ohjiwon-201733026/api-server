package com.gloomy.server.application.report;

public class ReportDTO {

    static class Request{
        private Long feedId;
        private String reportCategory;

        private Request(Long feedId, String reportCategory) {
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