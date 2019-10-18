package org.r.base.payment.service;


import org.r.base.payment.entity.RequestBo;
import org.r.base.payment.entity.RespondBo;
import org.r.base.payment.enums.RequestMethodEnum;

/**
 *
 * @author zj
 * @date 2019/3/27
 */
public interface HttpRequestService {


    /**
     * 获取请求处理单元
     *
     * @param requestMethod 请求方法
     * @return
     */
    HttpRequestStrategy getStrategy(RequestMethodEnum requestMethod);


    /**
     * 执行请求
     *
     * @param requestBo 请求信息
     * @return
     */
    RespondBo doRequest(RequestBo requestBo);


}
