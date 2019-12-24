# 1、简介

目前集成了微信,支付宝和paypal的支付。具体支持的支付方式看PaymentEnum类
### 三方支付及其支付方式

||支付宝|微信|paypal|
|---|---|---|---|
|APP支付|o|o|o|
|扫码支付|o|o|x|
|电脑网站支付|o|x|o|

# 2、使用说明

可以按需加载，需要的支付方式只要配置好相应的资料便可。
注意：在没有配置资料的情况下使用对应的支付方式会抛出空指针异常

# 3、配置方式
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
注意!直接import的方式产生的bean不是动态代理的bean,使用上需要注意配合动态代理

# 4、后续开发安排
## 新的三方支付
* 应该把支付的公共逻辑抽取出来形成一个抽象父类，不同的支付方式实现不同的细节


## 新的支付方式
* 继承已有的抽象父类，实现支付细节

## 配置参数
* 配置参数用一个配置bean存放，放在config包下，并可选择实现PaymentConfig接口
* 在注入配置bean的位置，注意不能强制注入，否则当该三方支付不启用的时候会有缺少注入的问题

# 5、有争议的点
* 现在三方的支付一般的流程是支付-回调流程，在回调过程中，同一个支付请求的回调可能不止一次。因此，这里处理回调的方法可以增加一个乐观锁。或者这个乐观锁也可以在业务系统实现。有待商议
* 同样地，也可以用aop技术，在调用与配置bean有关的方法时，先判断bean有没有被注入，这样可以避免误操作导致的空指针异常。不过，这个也是可以人为控制的。两种方案的成本不相伯仲，有待商议
