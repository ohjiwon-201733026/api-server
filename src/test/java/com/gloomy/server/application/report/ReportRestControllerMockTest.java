package com.gloomy.server.application.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gloomy.server.domain.report.ReportService;
import com.gloomy.server.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReportRestControllerMockTest {

    @InjectMocks
    private ReportRestController reportController;

    @Mock
    UserService userService;

    @Mock
    ReportService reportService;

    private MockMvc mockMvc; // API 요청 받기 위한 객체
    private ReportDTO.Request request;
    private final Long userId=10L;
    private final Long feedId=100L;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(reportController).build();
        request= ReportDTO.Request.of(feedId,"ADVERTISEMENT");
        objectMapper=new ObjectMapper();
    }

    @DisplayName("피드 신고하기 mock test")
    @Test
    public void reportFeed() throws Exception {
        // given
        doReturn(userId).when(userService).getMyInfo();

        // when
        final ResultActions resultActions=mockMvc
                .perform(MockMvcRequestBuilders.post("/report/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));


        // then
        final MvcResult mvcResult=resultActions.andExpect(status().isOk()).andReturn();

    }
}