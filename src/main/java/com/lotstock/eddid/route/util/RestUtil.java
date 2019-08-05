package com.lotstock.eddid.route.util;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;


@Component
public class RestUtil {

    private static RestTemplate restTemplate;

    @Autowired
    public RestUtil(RestTemplate restTemplate) {
        RestUtil.restTemplate = restTemplate;
    }

    public static <T> T postForObject(String url, Object object,Class<T> t) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MultiValueMap<String,String> multiValueMap = MapHelper.convertMapToMultiValueMap(BeanUtils.describe(object));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap> formEntity = new HttpEntity<>(multiValueMap, headers);
        return restTemplate.postForObject(url,formEntity, t);
    }
}
