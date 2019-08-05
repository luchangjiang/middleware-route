package com.lotstock.eddid.route.controller.fh;

import com.lotstock.eddid.api.eddidapi.dto.Page;
import com.lotstock.eddid.api.eddidapi.dto.fh.ReportDTO;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author ：River
 * @date ：Created in 5/27/2019 8:26 PM
 * @description：report of f10
 * @modified By：
 * @version: 1.0.1$
 */
@RestController
@RefreshScope
@RequestMapping("/fh/report")
@Api(tags={"研报信息"},value = "研报信息接口", description = "研报信息")
public class ReportController {
    protected static final Logger logger = Logger.getLogger(ReportController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyProps myProps;

    /**
     * 研报列表
     * @author River
     * @param dto  证券对象
     * @return
     */

    @ApiOperation(value = "研报列表", notes = "研报列表",response = R.class, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "marketType", value = "港股美股:HK/US", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "securitycode", value = "股票代碼", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "language", value = "简体: zh-Hans/繁体: zh-CN", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "分頁大小", required = true, dataType = "int", paramType = "query",example ="10"),
            @ApiImplicitParam(name = "start", value = "分頁從哪一行開始", required = true, dataType = "int", paramType = "query",example ="0")}
    )
    @GetMapping(value = "/page")
    public @ResponseBody
    R getReportPage(Page page, ReportDTO dto) {
        String urlParm="/report/page?"+"securitycode={1}&marketType={2}&size={3}&start={4}&language={5}";
        return restTemplate.getForObject(myProps.getFhServer() + urlParm, R.class
                , dto.getSecuritycode(),dto.getMarketType(),page.getSize(),page.getStart(),dto.getLanguage());
    }

    /**
     * 研报详情
     * @author River
     * @param eid  研报id
     * @return
     */
    @ApiOperation(value = "研报详情", notes = "研报详情",response = R.class, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "marketType", value = "港股美股:HK/US", required = true,  dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "eid", value = "文檔Id", required = true, dataType = "long", paramType = "path",example ="123" )}
    )
    @GetMapping(value = "/{marketType}/{eid}")
    public @ResponseBody
    R getReportContent(@PathVariable String marketType,@PathVariable String eid) {
        return restTemplate.getForObject(myProps.getFhServer() + "/report/{1}/{2}", R.class,marketType,eid);
    }
}
