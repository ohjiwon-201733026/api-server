package com.gloomy.server.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.domain.common.entity.Status;
import com.gloomy.server.domain.jwt.JWTDeserializer;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserService userServiceMock;
    @Mock
    UserRepository userRepository;
    @Mock
    JWTDeserializer jwtDeserializer;

    private MockWebServer mockWebServer;
    private ObjectMapper objectMapper;


    private Optional<User> testUser;
    private final Long userId=10L;
    private final Long notExistUserId=1234L;
    private User inactiveUser;
    private String token;
    private String nullToken;

    @BeforeEach
    public void setUp(){
        testUser=Optional.of(TestUserDTO.TestUser.makeTestUser());
        inactiveUser=testUser.get();
        inactiveUser.inactiveUser();
        token="test.token.mock";
        nullToken="";
    }

    @DisplayName("findUser mock success")
    @Test
    public void findUser_mock_success(){
        doReturn(testUser).when(userRepository).findByIdAndJoinStatus(userId, Status.ACTIVE);

        User saveUser=userService.findUser(userId);

        checkSameUser(saveUser,testUser.get());
    }

    private void checkSameUser(User actual, User expected){
        Assertions.assertEquals(actual.getEmail(),expected.getEmail());
        Assertions.assertEquals(actual.getName(),expected.getName());
    }

    @DisplayName("findUser mock fail")
    @Test
    public void findUser_mock_fail(){
        doThrow(IllegalArgumentException.class).when(userRepository).findByIdAndJoinStatus(notExistUserId,Status.ACTIVE);

        assertThrows(IllegalArgumentException.class,
                ()->userService.findUser(notExistUserId));
    }

    @DisplayName("deleteUser mock success")
    @Test
    public void deleteUser_mock_success(){
        doReturn(testUser).when(userRepository).findByIdAndJoinStatus(userId, Status.ACTIVE);
        doNothing().when(userRepository).delete(testUser.get());

        userService.deleteUser(userId);

    }

    @DisplayName("deleteUser mock fail")
    @Test
    public void deleteUser_mock_fail(){
        doThrow(IllegalArgumentException.class).when(userRepository).findByIdAndJoinStatus(notExistUserId, Status.ACTIVE);

        assertThrows(IllegalArgumentException.class,
                ()->userService.deleteUser(notExistUserId));
    }

    @DisplayName("inactive mock success")
    @Test
    public void inactive_mock_success(){

        doReturn(testUser).when(userRepository).findByIdAndJoinStatus(userId,Status.ACTIVE);
        doReturn(inactiveUser).when(userRepository).save(testUser.get());

        User saveUser=userService.inactiveUser(userId);

        checkSameUser(saveUser,inactiveUser);
    }

    @DisplayName("inactive mock fail")
    @Test
    public void inactive_mock_fail(){

        doReturn(testUser).when(userRepository).findByIdAndJoinStatus(userId,Status.ACTIVE);
        doThrow(IllegalArgumentException.class).when(userRepository).save(testUser.get());

        assertThrows(IllegalArgumentException.class,()->userService.inactiveUser(userId));

    }
//    @Mock UserJWTPayload userJWTPayload;


//    @DisplayName("jwt user id 가져오기 success")
//    @Test
//    public void getMyInfo_mock_success(){
//        doReturn(token).when(userServiceMock).getToken();
//        doReturn(new UserJWTPayload()).when(jwtDeserializer).jwtPayloadFromJWT(token);
//        doReturn(userId).when(userJWTPayload).getUserId();
//
//        Long getUserId=userService.getMyInfo();
//
//        Assertions.assertEquals(getUserId,userId);
//    }
}