package com.lotstock.eddid.route.controller.fh;

import com.lotstock.eddid.api.eddidapi.dto.Page;
import com.lotstock.eddid.api.eddidapi.dto.fh.NoticeDTO;
import com.lotstock.eddid.route.config.MyProps;
import com.lotstock.eddid.route.util.R;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author ：River
 * @date ：Created in 5/27/2019 8:26 PM
 * @description：notice of f10
 * @modified By：
 * @version: 1.0.1$
 */
@RestController
@RefreshScope
@RequestMapping("/fh/notice")
@Api(tags={"公告信息"},value = "公告信息接口", description = "公告信息")
public class NoticeController {
    protected static final Logger logger = Logger.getLogger(NoticeController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyProps myProps;

    /**
     * 公告列表
     * @author River
     * @param dto  证券对象
     * @return
     */
    @ApiOperation(value = "公告列表", notes = "公告列表",response = R.class, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "marketType", value = "港股美股:HK/US", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "securitycode", value = "股票代碼", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "language", value = "简体: zh-Hans/繁体: zh-CN", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "分頁大小", required = true, dataType = "int", paramType = "query",example ="10"),
            @ApiImplicitParam(name = "start", value = "分頁從哪一行開始", required = true, dataType = "int", paramType = "query",example ="0")}
    )
    @GetMapping(value = "/page")
    public @ResponseBody
    R getNoticePage (Page page, NoticeDTO dto) {
        String urlParm="/notice/page?"+"securitycode={1}&marketType={2}&size={3}&start={4}&language={5}";
        return restTemplate.getForObject(myProps.getFhServer() + urlParm, R.class
                , dto.getSecuritycode(),dto.getMarketType(),page.getSize(),page.getStart(),dto.getLanguage());
    }

    /**
     * 公告详情
     * @author River
     * @param eid  公告id
     * @return
     */
    @ApiOperation(value = "公告详情", notes = "公告详情",response = R.class, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "marketType", value = "港股美股:HK/US", required = true,  dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "eid", value = "文檔Id", required = true, dataType = "long", paramType = "path",example ="123" )}
    )
    @GetMapping(value = "/{marketType}/{eid}")
    public @ResponseBody
    R getNoticeContent(@PathVariable String marketType,@PathVariable long eid) {
        return restTemplate.getForObject(myProps.getFhServer() + "/notice/{1}/{2}", R.class,marketType, eid);
    }
}
