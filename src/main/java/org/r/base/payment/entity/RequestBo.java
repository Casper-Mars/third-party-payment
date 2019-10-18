package org.r.base.payment.entity;

import lombok.Data;
import org.r.base.payment.enums.ProtocolEnum;
import org.r.base.payment.enums.RequestMethodEnum;

/**
 * 请求信息
 *
 * @author casper
 * @date 19-10-16 下午2:06
 **/
@Data
public class RequestBo {


    /**
     * 请求地址
     */
    private String url;

    /**
     * 协议类型
     */
    private ProtocolEnum protocol;

    /**
     * 请求方法
     */
    private RequestMethodEnum method;

    /**
     * 参数
     */
    private Object param;


}
