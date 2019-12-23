# 简介

目前集成了微信,支付宝和paypal的支付。具体支持的支付方式看PaymentEnum类

# 使用说明

可以按需加载，需要的支付方式只要配置好相应的资料便可。
注意：在没有配置资料的情况下使用对应的支付方式会抛出空指针异常

# 配置方式
在spring的配置文件注入三方支付的配置

## 例子
     <!--微信支付配置-->
        <bean id="wechatPayConfig" class="org.r.base.payment.config.WechatPayConfig">
            <property name="apiKey" value="${wechat.apiKey}"/>
            <property name="appSecret" value="${wechat.appSecret}"/>
            <property name="gatewayUrl" value="${wechat.gatewayUrl}"/>
            <property name="refundUrl" value="${wechat.refundUrl}"/>
            <property name="mchid" value="${wechat.mchid}"/>
            <property name="requestMethod" value="${wechat.requestMethod}"/>
            <property name="appId" value="${wechat.appId}"/>
            <property name="p12CertPath" value="${wechat.p12CertPath}"/>
        </bean>
    
        <!--阿里支付配置-->
        <bean id="alipayConfig" class="org.r.base.payment.config.AlipayConfig">
            <property name="appId" value="${alipay.appId}"/>
            <property name="returnUrl" value="${alipay.returnUrl}"/>
            <property name="outOfTime" value="${alipay.outOfTime}"/>
            <property name="partner" value="${alipay.partner}"/>
            <property name="privateKey" value="${alipay.privateKey}"/>
            <property name="aliPublicKey" value="${alipay.aliPublicKey}"/>
            <property name="inputCharset" value="${alipay.inputCharset}"/>
            <property name="signType" value="${alipay.signType}"/>
            <property name="sellerId" value="${alipay.sellerId}"/>
            <property name="format" value="${alipay.format}"/>
            <property name="gatewayUrl" value="${alipay.gatewayUrl}"/>
        </bean>

## 或者
     <import resource="classpath*:payment-context.xml" />
### 注意!直接import的方式产生的bean不是动态代理的bean,使用上需要注意配合动态代理

