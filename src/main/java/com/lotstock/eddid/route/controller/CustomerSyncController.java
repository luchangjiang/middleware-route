package com.lotstock.eddid.route.controller;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ResultModel;
import com.lotstock.eddid.route.vo.SyncCustomerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@EnableAsync
@RestController
@RefreshScope
@RequestMapping("/customerSync")
public class CustomerSyncController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyProps myProps;

    /**
     * 同步客户数据推送
     * @param voList
     * @return
     */
//    @Async
    @PostMapping(value = "/sendData")
    @ResponseBody
    public ResultModel sendData(@RequestBody List<SyncCustomerVo> voList) {
        String url = myProps.getCustomerServer() + "/sync/sendData";
        ResultModel resultModel = restTemplate.postForObject(url, voList, ResultModel.class);
        logger.info(String.valueOf(resultModel.code));

        return resultModel;
    }
}
