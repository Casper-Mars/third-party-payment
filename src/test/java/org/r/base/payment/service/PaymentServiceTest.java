package org.r.base.payment.service;


import com.alipay.api.AlipayApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.r.base.payment.config.AlipayConfig;
import org.r.base.payment.entity.PayCommon;
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

    @Test
    public void pay() {
        PayCommon payCommon = new PayCommon(
                "testsn1231",
                new BigDecimal("0.01"),
                "http://39.108.88.133/api/api/notify",
                "test pay",
                "test pay",
                "39.108.88.133"
        );
        String pay = "";
        try {
//            pay = amp.pay(payCommon);
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
    }

    @Test
    public void refundNotifyCallBack() {
    }
}