package com.gloomy.server.application.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.jwt.JwtService;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.User;
import com.gloomy.server.domain.user.UserService;
import com.gloomy.server.domain.user.login.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static com.gloomy.server.domain.user.login.LoginFixture.ACCESS_TOKEN;
import static com.gloomy.server.domain.user.login.LoginFixture.USER_ID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserRestControllerMockTest {

    @InjectMocks
    UserRestController userRestController;

    @Mock
    LoginService loginService;
    @Mock
    UserService userService;
    @Mock
    JwtService jwtService;
    @Mock
    JWTSerializer jwtSerializer;
    @Mock
    User user;
    @Mock
    UserDTO.Response response;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserDTO.CodeRequest request;
    private User testUser;
    private String accessToken;

    @BeforeEach
    public void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(userRestController).build();
        objectMapper=new ObjectMapper();
        request=new UserDTO.CodeRequest("code","redirect_uri");
        testUser=TestUserDTO.TestUser.makeTestUser();
        testUser.changeId(USER_ID);
        testUser.changeRefreshToken(ACCESS_TOKEN);
        accessToken=ACCESS_TOKEN;

    }

//    @DisplayName("카카오 로그인")
//    @Test
//    public void kakaoLogin() throws Exception {
//
//        UserDTO.Response res= UserDTO.Response.fromUserAndToken(testUser,ACCESS_TOKEN,ACCESS_TOKEN);
//
//        doReturn(testUser).when(loginService).login(request);
//        doReturn(ACCESS_TOKEN).when(jwtSerializer).jwtFromUser(testUser);
//        doReturn(ACCESS_TOKEN).when(user).getRefreshToken();
//        doReturn(res).when(response).fromUserAndToken(testUser,ACCESS_TOKEN,ACCESS_TOKEN);
//
//        ResultActions resultActions=mockMvc.perform(MockMvcRequestBuilders.post("/kakao/signUp")
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        MvcResult mvcResult=resultActions.andExpect(status().isOk()).andReturn();
//
//        System.out.println(mvcResult.getResponse().getContentAsString()
//        );
//
//    }

    @DisplayName("logout")
    @Test
    public void logout() throws Exception {
        doNothing().when(loginService).logout();

        mockMvc.perform(get("/kakao/logout")
                .header("Authorization","Bearer "+accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("inactive user")
    @Test
    public void inactiveUser() throws Exception {
        doReturn(USER_ID).when(jwtService).getMyInfo();
        doReturn(testUser).when(userService).inactiveUser(USER_ID);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/inactive")
                .header("Authorization","Bearer "+accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
