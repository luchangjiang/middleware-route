package com.lotstock.eddid.route.controller.ayers;
/*
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.controller.fh.NoticeController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;*/


/**
 * @author ：Archie,Fu
 * @date ：Created in 6/24/2019 16:26
 * @description：notice of f10
 * @modified By：
 * @version: 1.0.1$
 */
/*
@RestController
@RefreshScope*/
/*@RequestMapping("/ayersgate")*/
public class AyersQueryController {/*
    protected static final Logger logger = Logger.getLogger(NoticeController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyProps myProps;

    *//**
     * Ayers Stock Query
     * @author Archie,Fu
     * @return
     *//*

    @RequestMapping(value = "/way123")
    public @ResponseBody
    ResponseEntity<String> getAyersList(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.put("QueryString", Collections.singletonList(request.getParameter("QueryString")));

        if(request.getSession()!=null && request.getSession().getAttribute("USessionNo")!=null){
            int uSessionNo = Integer.parseUnsignedInt(request.getSession().getAttribute("USessionNo").toString());
            headers.put("USessionNo", Collections.singletonList(String.valueOf(uSessionNo)));
        }
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(map, headers);
        if(myProps.getAyersServer()==null){
            logger.error("no myProps!!");
        }
        logger.info("myProps.getAyersServer():"+myProps.getAyersServer());
        ResponseEntity<String> res = restTemplate.postForEntity( myProps.getAyersServer() + "/ayersgate/way", req , String.class );

        return res*//*new R<>(res)*//*;
    }*/
}