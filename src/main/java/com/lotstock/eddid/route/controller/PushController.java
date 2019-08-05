package com.lotstock.eddid.route.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.Re;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.util.StrUtils;

/**
 * 消息
 *
 * @author David
 */
@RestController
@RefreshScope
@RequestMapping("/push")
public class PushController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyProps myProps;


    @RequestMapping(value = "/updateDevice", method = RequestMethod.POST)
    @ResponseBody
    public Re updateDevice(String userid, String deviceToken, String type) {
        Map params = new HashMap();
        params.put("userid", userid);
        params.put("deviceToken", deviceToken);
        params.put("type", type);
        String url = myProps.getMessageServer() + "/push/updateDevice";
        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(url, entity, Re.class).getBody();
    }


    /**
     * {
     * "users":["10"],
     * "title":"请输入通知栏标题",
     * "content":"请输入通知栏内容",
     * "transContent":"请输入您要透传的内容"
     * }
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    @ResponseBody
    public Re execute(String users, String title, String content, String transContent, String msgType) {
        Map params = new HashMap();
//        String[] userList = users.split(",");
        params.put("users", users);
        params.put("content", content);
        params.put("transContent", transContent);
        params.put("title", title);
        params.put("msgType", msgType);
        String url = myProps.getMessageServer() + "/push/execute";
        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(url, entity, Re.class).getBody();
//        return restTemplate.postForEntity(myProps.getMessageServer() + "/push/execute", vo, Re.class).getBody();
    }

    /**
     * {
     * "msgType":100,
     * "pageSize":10,
     * "pageNum":0
     * }
     *
     * @return
     */
    @PostMapping(value = "/info")
    @ResponseBody
    public Re info(String msgType, String pageSize, String pageNum) {
        Map params = new HashMap();
        params.put("msgType", msgType);
        params.put("pageSize", pageSize);
        params.put("pageNum", pageNum);
        String url = myProps.getMessageServer() + "/push/info";
        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(url, entity, Re.class).getBody();
    }


    @RequestMapping(value = "/typeInfo", method = RequestMethod.POST)
    @ResponseBody
    public Re typeInfo(String size) {
        Map params = new HashMap();
        params.put("size", size);
        String url = myProps.getMessageServer() + "/push/typeInfo";
        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(url, entity, Re.class).getBody();
    }

    /**
     * {
     * "infoId":1,
     * "userId":129,
     * "infoType":100
     * }
     *
     * @return
     */
    @RequestMapping(value = "/readInfo", method = RequestMethod.POST)
    @ResponseBody
    public Re readInfo(String userId, String infoId, String infoType) {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("infoId", infoId);
        params.put("infoType", infoType);
        String url = myProps.getMessageServer() + "/push/readInfo";
        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(url, entity, Re.class).getBody();
    }


    @RequestMapping(value = "/unReadNum", method = RequestMethod.POST)
    @ResponseBody
    public Re unReadNum(String userId) {
        Map params = new HashMap();
        params.put("userId", userId);
        String url = myProps.getMessageServer() + "/push/unReadNum";
        //
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, headers);
        return restTemplate.postForEntity(url, entity, Re.class).getBody();
    }


    @PostMapping(value = "/addInfo")
    @ResponseBody
    public Re addInfo(@RequestBody JSONObject object) throws Exception{
    	return restTemplate.postForEntity(myProps.getMessageServer() + "/push/addInfo",object,Re.class).getBody();
    }
}
