package org.r.base.payment.service;


import com.alipay.api.AlipayApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.r.base.payment.config.AlipayConfig;
import org.r.base.payment.entity.PayCommon;
import org.r.base.payment.entity.RefundCommon;
import org.r.base.payment.exception.PayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class PaymentServiceTest {

    @Autowired
    @Qualifier("alipayMobilePaymentPlugin")
    private PaymentService amp;

    @Autowired
    @Qualifier("alipayScanPaymentPlugin")
    private PaymentService asp;

    @Autowired
    @Qualifier("wechatMobilePaymentPlugin")
    private PaymentService wmp;

    @Autowired
    @Qualifier("wechatScanPaymentPlugin")
    private PaymentService wsp;

    @Autowired
    private AlipayConfig alipayConfig;

    String paySn = "POD20191016782539";

    @Test
    public void pay() {
        PayCommon payCommon = new PayCommon(
                paySn,
                new BigDecimal("0.01"),
                "http://39.108.88.133/payment/post/param/" + paySn,
                "test pay",
                "test pay",
                "39.108.88.133"
        );
        String pay = "";
        try {
//            pay = amp.pay(payCommon);1
//            System.out.println(pay);
//            pay = asp.pay(payCommon);
//            System.out.println(pay);
//            pay = wmp.pay(payCommon);
//            System.out.println(pay);
            pay = wsp.pay(payCommon);
            System.out.println(pay);
        } catch (PayException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void notifyCallBack() {
    }

    @Test
    public void refund() {

        RefundCommon refundCommon = new RefundCommon();
        refundCommon.setTraceNo("4200000431201910244485558526");
        refundCommon.setRefundFee(new BigDecimal("0.01"));
        refundCommon.setOrderFee(new BigDecimal("0.01"));
        refundCommon.setOutTraceNo(paySn);
        refundCommon.setNotifyUrl("http://39.108.88.133/payment/post/param/" + paySn);
        refundCommon.setOutRefundNo("refund"+paySn+"123");

        Boolean refund = false;
        try {
            refund = wsp.refund(refundCommon);
        } catch (PayException e) {
            e.printStackTrace();
        }
        System.out.println(refund ? "退款成功" : "退款失败");
    }

    @Test
    public void refundNotifyCallBack() {
    }
}