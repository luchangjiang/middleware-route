package com.lotstock.eddid.route.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SyncCustomerVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String customerId;
    private String nickName;
    private String mobile;

    //缺少此项会报 No default construct error
    public SyncCustomerVo(){}

}
