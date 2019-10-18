package org.r.base.payment.service;

import com.alipay.api.AlipayApiException;
import org.r.base.payment.dto.NotifyDTO;
import org.r.base.payment.entity.PayCommon;
import org.r.base.payment.entity.RefundCommon;
import org.r.base.payment.exception.PayException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author casper
 */
public interface PaymentService {

    /**
     * 支付接口
     *
     * @param payCommon 支付参数，不同实现类参数不同
     * @return
     */
    String pay(PayCommon payCommon) throws PayException;


    /**
     * 给第三方支付调用的回调方法
     *
     * @param request  请求对象
     * @param billSn   账单唯一标识，此处标识id
     * @param response 响应对象
     * @return
     */
    NotifyDTO notifyCallBack(HttpServletRequest request, HttpServletResponse response, String billSn);


    /**
     * 退款接口
     *
     * @param refundCommon 退款的参数
     * @return
     */
    Boolean refund(RefundCommon refundCommon);


    /**
     * 退款的通知回调
     *
     * @param request
     * @param response
     * @param billSn   退款唯一标志
     * @return
     */
    NotifyDTO refundNotifyCallBack(HttpServletRequest request, HttpServletResponse response, String billSn);


}
