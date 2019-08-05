package com.lotstock.eddid.route.controller.fh;

import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.R;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author ：Wade
 * @date ：Created in 6/6/2019 16:26
 * @description：notice of f10
 * @modified By：
 * @version: 1.0.1$
 */

@RestController
@RefreshScope
@RequestMapping("/fh/company")
public class CompanyController {
    protected static final Logger logger = Logger.getLogger(NoticeController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyProps myProps;


    /**
     * 股票基本资料
     * @author wade
     * @param companysName  机构简称
     * @return
     */
//    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @GetMapping(value = "/info")
    public @ResponseBody
    R getNoticeList(String stockCode, String marketType) throws Exception {
        return restTemplate.getForObject(myProps.getFhServer() + "/company/info?stockCode={1}&marketType={2}", R.class, stockCode, marketType);
    }
}
