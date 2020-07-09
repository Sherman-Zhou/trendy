package com.joinbe.service.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@Component
public class RestfulClient {

    private final RestTemplate restTemplate;

    private ObjectMapper mapper;

    public RestfulClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    private final Logger log = LoggerFactory.getLogger(RestfulClient.class);

    public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        log.debug("start to call Restful Service: Url= {}, request={}", url, request);
        T t = restTemplate.postForObject(url, request, responseType);

        checkResponse(t);
        log.debug("start to send response from  Restful Service:  response={}", t);
        return t;
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException{
        log.debug("start to call Restful Service: Url= {}, request={}", url, request);
        ResponseEntity<T> t =  restTemplate.postForEntity(url, request, responseType);

        log.debug("start to send response from  Restful Service:  response={}", t);
        return t;
    }



    public <T> T postForObject(String url, Object request, Class<T> responseType) throws RestClientException{
        log.debug("start to call Restful Service: Url= {}, request={}", url, request);
        T t = restTemplate.postForObject(url, request, responseType);
        checkResponse(t);
        log.debug("start to send response from  Restful Service:  response={}", t);

        return t;
    }

    public <T> T getForObject(String url, Class<T> responseType, Map<String, String> uriVariables)
            throws RestClientException {

        StringBuilder getUrl = new StringBuilder();
        getUrl.append(url + "?");
        Set<String> keys = uriVariables.keySet();
        for (String key : keys) {
            getUrl.append(key + "=" + UriUtils.encodeQueryParam(uriVariables.get(key), StandardCharsets.UTF_8) + "&");
        }
        getUrl.deleteCharAt(getUrl.length() - 1);
        T t = restTemplate.getForObject(getUrl.toString(), responseType);

        checkResponse(t);
        return t;

    }
    public<T> ResponseEntity<T> getForEntity(String url, Class<T> responseType)
        throws RestClientException {


        ResponseEntity<T> t = restTemplate.getForEntity(url, responseType);
        // checkResponse(t);
        return t;

    }

    public <T> T getForObject(String url, Class<T> responseType)
            throws RestClientException {


        T t = restTemplate.getForObject(url, responseType);
       // checkResponse(t);
        return t;

    }

    public <T> T getForObjectWithJson(String url, Object uriVariables, Class<T> responseType)
            throws RestClientException {
        T t = null;
        StringBuilder getUrl = new StringBuilder(url);

        String reqeust = getJsonString(uriVariables);
        if (reqeust != null) {
            getUrl.append("?request=" + reqeust);
        }
        try {
            String content = httpGet(getUrl.toString());
            log.debug("response content= {}", content);
            t = mapper.readValue(content, responseType);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        checkResponse(t);
        return t;

    }

    private <T> void checkResponse(T t) {

        if (t instanceof ResponseBody) {
            ResponseBody response = (ResponseBody) t;
//            if (response == null || response.getResult().getCode() != 0) {
//                log.error(" response has error....{}", response.getResult().getMessage());
//            }
        }
    }

    private String getJsonString(Object params) {
        String json = null;
        try {

            String tmp = mapper.writeValueAsString(params);
            json = URLEncoder.encode(tmp, StandardCharsets.UTF_8.toString());
            log.debug("request json: {}", tmp);

        } catch (Exception e) {
            throw new RuntimeException("error when converting to json");
        }
        return json;
    }

    private String httpGet(String requestUrl) throws Exception {
        URL url = new URL(requestUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        return IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
    }

}
