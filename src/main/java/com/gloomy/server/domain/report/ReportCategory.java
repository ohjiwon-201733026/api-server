package com.gloomy.server.domain.report;


public enum ReportCategory {

    A("광고, 홍보성 내용"),
    B("욕설, 외설적 언어 사용"),
    C("선정적, 폭력적인 내용"),
    D("도배성 내용"),
    E("정치적 사회적 의견 표출");

    private String title;

    ReportCategory(String title) {
        this.title = title;
    }


}