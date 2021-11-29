package com.gloomy.server.application;

import com.gloomy.server.ApiServerApplication;
import com.gloomy.server.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@ActiveProfiles
@Import(TestConfig.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(classes = ApiServerApplication.class)
public abstract class AbstractControllerTest {

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    protected MockMvc mockMvc;

    protected RestDocumentationResultHandler document;

    @BeforeEach
    protected void setUp(WebApplicationContext webAppContext, RestDocumentationContextProvider restDocumentation) {
        this.document = document(
                "{class-name}/{method-name}",
                preprocessResponse(prettyPrint()));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(documentationConfiguration(restDocumentation))
                .addFilters(new CharacterEncodingFilter("UTF-8", true), springSecurityFilterChain)
                .alwaysDo(document).build();
    }
}
