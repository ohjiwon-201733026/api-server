package com.gloomy.server.domain.user;

import com.gloomy.server.application.feed.TestFeedDTO;
import com.gloomy.server.application.user.TestUserDTO;
import com.gloomy.server.application.user.UserDTO;
import com.gloomy.server.domain.common.entity.Status;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gloomy.server.application.user.TestUserDTO.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    User user;
    UserDTO.UpdateUserDTO.Request updateUserDTO;
    TestFeedDTO testFeedDTO;
    final Long notExistUserId=-1L;


    @BeforeEach
    public void setUp(){
        this.user= TestUser.makeTestUser();
        this.updateUserDTO= UpdateUserTestDTO.makeUpdateUserDtoRequest();
        testFeedDTO = new TestFeedDTO(user, 1);
    }


    @DisplayName("user create 성공")
    @Test
    public void user_create_success(){
        User saveUser=userService.createUser(user);

        User findUser=userService.findUser(saveUser.getId());

        checkSameUser(findUser,saveUser);
    }

    private void checkSameUser(User actual,User expect){
        Assertions.assertEquals(actual.getId(),expect.getId());
        Assertions.assertEquals(actual.getEmail(),expect.getEmail());
        Assertions.assertEquals(actual.getJoinStatus(),expect.getJoinStatus());
    }


    @DisplayName("user find 성공")
    @Test
    public void user_find_success(){
        User saveUser=userService.createUser(user);

        User findUser=userService.findUser(saveUser.getId());

        checkSameUser(saveUser,findUser);
    }

    @DisplayName("user find 실패 : 존재하지 않는 유저")
    @Test
    public void user_find_fail(){
        assertThrows(IllegalArgumentException.class,
                ()->userService.findUser(notExistUserId));
    }


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
                ()->userService.deleteUser(notExistUserId));
    }

    @DisplayName("user deleteAll 성공")
    @Test
    public void user_deleteAll(){

        for(int i=0;i<5;++i) userService.createUser(user);

        userService.deleteAll();

        List<User> userList=userRepository.findAll();

        Assertions.assertEquals(userList.size(),0);
    }

    @DisplayName("user inactive(탈퇴) 성공")
    @Test
    public void user_inactive_success(){
        User saveUser= userService.createUser(user);

        User inactiveUser=userService.inactiveUser(saveUser.getId());

        Assertions.assertEquals(Status.INACTIVE, inactiveUser.getJoinStatus());
    }

    @DisplayName("user inactive(탈퇴) 실패")
    @Test
    public void user_inactive_fail(){
        assertThrows(IllegalArgumentException.class,
                ()->userService.inactiveUser(notExistUserId));
    }

    @Test
    @DisplayName("닉네임 생성")
    public void nicknameCreate(){
        String nickname=userService.createNickName();
        Assertions.assertEquals(nickname!=null,true);
    }




}