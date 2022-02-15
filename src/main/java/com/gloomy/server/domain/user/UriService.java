package com.gloomy.server.domain.user;

import lombok.Builder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;

@Service
@Transactional
public class UriService {

    public URI getUri(String baseUriTemplate, String uriTemplate, MultiValueMap<String,String> params){
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(baseUriTemplate);
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return uriBuilderFactory.uriString(uriTemplate).queryParams(params).build();
    }

}
