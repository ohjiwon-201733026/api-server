package com.gloomy.server.application.user;

import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.image.UserProfileImageService;
import com.gloomy.server.domain.jwt.JWTSerializer;
import com.gloomy.server.domain.user.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.config.location=classpath:test-application.yml,classpath:aws.yml"
})
@Transactional
public class UserServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    JWTSerializer jwtSerializer;
    User user;
    UserDTO.UpdateUserDTO.Request updateUserDTO;
    TestFeedDTO testFeedDTO;
    final Long notExistUserId=100L;


    @BeforeEach
    public void setUp(){
        this.user= TestUserDTO.TestUser.makeTestUser();
        this.updateUserDTO= TestUserDTO.UpdateUserTestDTO.makeUpdateUserDtoRequest();
        testFeedDTO = new TestFeedDTO(user, 1);

    }

//    @AfterEach
//    public void after(){
//        userProfileImageService.deleteAll(user);
//    }

    @DisplayName("user create 성공")
    @Test
    public void user_create_success(){
        User saveUser=userService.createUser(user);

        User findUser=userService.findUser(saveUser.getId());

        checkSameUser(findUser,saveUser);
    }

    private void checkSameUser(User actual,User expect){
        Assertions.assertEquals(actual.getId(),expect.getId());
        Assertions.assertEquals(actual.getSex(),expect.getSex());
        Assertions.assertEquals(actual.getEmail(),expect.getEmail());
        Assertions.assertEquals(actual.getDateOfBirth(),expect.getDateOfBirth());
        Assertions.assertEquals(actual.getJoinStatus(),expect.getJoinStatus());
    }



    @DisplayName("user update 성공")
    @Test
    public void user_update_success(){
        User saveUser=userService.createUser(user);
        String token=jwtSerializer.jwtFromUser(saveUser);

        userService.updateUser(saveUser.getId(),updateUserDTO);

        User updateUser=userService.findUser(saveUser.getId());
        checkUpdateUser(updateUser);
    }

    @DisplayName("user update 실패 : 유효하지 않은 user")
    @Test
    public void user_update_fail(){
        user.changeId(notExistUserId);
        String token=jwtSerializer.jwtFromUser(user);

        assertThrows(IllegalArgumentException.class,
                ()->userService.updateUser(user.getId(),updateUserDTO));
    }

    private void checkUpdateUser(User user){
        Assertions.assertEquals(user.getEmail(),updateUserDTO.getEmail());
        Assertions.assertEquals(user.getSex(),updateUserDTO.getSex());
        Assertions.assertEquals(user.getDateOfBirth().toString(),updateUserDTO.getDateOfBirth());
    }

    @DisplayName("user find 성공")
    @Test
    public void user_find_success(){
        User saveUser=userService.createUser(user);

        User findUser=userService.findUser(saveUser.getId());

        checkSameUser(saveUser,findUser);
    }
    /*
    @DisplayName("user find 실패 : 존재하지 않는 유저")
    @Test
    public void user_find_fail(){
        assertThrows(IllegalArgumentException.class,
                ()->userService.findUser(notExistUserId));
    }
     */

    @DisplayName("user delete 성공")
    @Test
    public void user_delete_success(){
        User saveUser= userService.createUser(user);

        userService.deleteUser(saveUser.getId());

        assertThrows(IllegalArgumentException.class,
                ()->userService.findUser(notExistUserId));
    }

    @DisplayName("user delete 실패 : 존재하지 않는 사용자")
    @Test
    public void user_delete_fail(){
        user.changeId(notExistUserId);

        assertThrows(IllegalArgumentException.class,
                ()->userService.deleteUser(user.getId()));
    }

    @DisplayName("user logout")
    @Test
    public void user_logout(){
        Optional<User> test= Optional.of(userRepository.save(User.of("email", "nickname", "token")));
    }

    @Test
    @DisplayName("닉네임 생성")
    public void nicknameCreate(){
        String nickname=(String)userService.nicknameCreate();
        Assertions.assertEquals(nickname!=null,true);
    }




}
