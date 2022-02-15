package com.gloomy.server.application.user.login;

import com.gloomy.server.application.user.UserDTO;

public class LoginFixture {
    public static final String CODE = "code";
    public static final String CODE_VALUE = "CODE";
    public static final String URL = "http:localhost:8080";
    public static final String GUEST_TOKEN = "SAMPLE_ACCESS_GUEST_TOKEN";
    public static final String USER_TOKEN = "SAMPLE_ACCESS_USER_TOKEN";
    public static final String ADMIN_TOKEN = "SAMPLE_ACCESS_ADMIN_TOKEN";
    public static final String LOGIN_SUCCESS = "true";
    public static final String LOGIN_FAIL = "false";
    public static final String SERVER_URI = "http://localhost:8080";
    public static final String CLIENT_ID_VALUE = "1231234";
    public static final String CLIENT_SECRET_VALUE = "SECRET";
    public static final String GRANT_TYPE_VALUE = "code";
    public static final String OAUTH_TOKEN_PATH = "/oauth/token";
    public static final String LOGIN_CHECK_PATH = "/api/login/check";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SUCCESS = "success";
    public static final String IS_CREATED = "is_created";
    public static final String USER_INFO_PATH = "/v2/user/me";
    public static final String TOKEN_TYPE = "Bearer ";
    public static final String SCOPE = "scope ";
    public static final String NICKNAME = "nickname";
    public static final String BIRTHDAY = "0429";
    public static final boolean EXIST = true;
    public static final boolean NOT_EXIST = false;
    public static final int EXPIRE = 1;
    public static final  Long KAKAO_ID = 1038582L;
    public static final  String EMAIL = "TEST@GMAIL.COM";
    public static final String CONNECTED_AT="2020-02-02 00:00:00";
    public static final Long USER_ID=100L;
    public static final String LOGOUT_PATH="/v1/user/logout";

    public static UserDTO.KakaoToken createMockKakaoTokenResponse(){
        return  UserDTO.KakaoToken.builder()
                .token_type(TOKEN_TYPE)
                .access_token(USER_TOKEN)
                .expires_in(EXPIRE)
                .refresh_token(USER_TOKEN)
                .refresh_token_expires_in(EXPIRE)
                .scope(SCOPE)
                .build();
    }

    public static UserDTO.KakaoUser createMockKakaoUserResponse() {
        return UserDTO.KakaoUser.builder()
                .id(KAKAO_ID)
                .connected_at(CONNECTED_AT)
                .kakao_account(UserDTO.KakaoAccount.builder()
                        .email_needs_agreement(false)
                        .profile_nickname_needs_agreement(false)
                        .profile(UserDTO.Profile.builder().nickname(NICKNAME).build())
                        .is_email_valid(false)
                        .is_email_verified(false)
                        .has_email(false)
                        .email(EMAIL).build())
                .build();

    }
}
