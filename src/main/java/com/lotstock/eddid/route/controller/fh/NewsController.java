package com.lotstock.eddid.route.controller.fh;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.ResultModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author ：River
 * @date ：Created in 5/27/2019 8:25 PM
 * @description：news of f10
 * @modified By：
 * @version: 1.0.1$
 */
@RestController
@RefreshScope
@RequestMapping("/fh/news")
public class NewsController {
    protected static final Logger logger = Logger.getLogger(NewsController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyProps myProps;

    /**
     * 新闻列表
     * @author River
     * @param securityCode  证券代码
     * @param securityName  证券代码
     * @return
     */
    @GetMapping(value = "/list/{securityCode}/{securityName}")
    public @ResponseBody
    ResultModel getNewsList(@PathVariable String securityCode, @PathVariable String securityName) throws Exception {
        return restTemplate.getForObject(myProps.getFhServer() + "/fh/news/list?securityCode={1}&securityName={2}", ResultModel.class,securityCode,securityName);
    }

    /**
     * 新闻详情
     * @author River
     * @param eid  新闻id
     * @return
     */
    @GetMapping(value = "/content")
    public @ResponseBody
    ResultModel getNewsContent(@PathVariable long eid) throws Exception {
        return restTemplate.getForObject(myProps.getFhServer() + "/news/content", ResultModel.class, eid);
    }
}
