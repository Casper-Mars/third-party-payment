package org.r.base.payment.service.impl.wechat;

import lombok.extern.slf4j.Slf4j;
import org.jdom.Element;
import org.r.base.payment.config.WechatPayConfig;
import org.r.base.payment.dto.NotifyDTO;
import org.r.base.payment.entity.PayCommon;
import org.r.base.payment.entity.RefundCommon;
import org.r.base.payment.entity.RequestBo;
import org.r.base.payment.entity.RespondBo;
import org.r.base.payment.enums.ProtocolEnum;
import org.r.base.payment.enums.RequestMethodEnum;
import org.r.base.payment.exception.PayException;
import org.r.base.payment.service.HttpRequestService;
import org.r.base.payment.service.PaymentService;
import org.r.base.payment.utils.XDigestUtils;
import org.r.base.payment.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author casper
 * @date 19-10-18 下午2:07
 **/
@Slf4j
public abstract class AbstractWechatServiceImpl implements PaymentService {

    @Autowired
    protected WechatPayConfig wechatPayConfig;
    @Autowired
    private HttpRequestService httpRequestService;


    /**
     * 获取交易类型:app,native
     *
     * @return
     */
    protected abstract String getTradeType();

    /**
     * 构造请求参数
     * <xml>
     * <appid>wx2421b1c4370ec43b</appid>
     * <body>APP支付测试</body>
     * <mch_id>10000100</mch_id>
     * <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
     * <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
     * <out_trade_no>1415659990</out_trade_no>
     * <spbill_create_ip>14.23.150.211</spbill_create_ip>
     * <total_fee>1</total_fee>
     * <trade_type>APP</trade_type>
     * <sign>0CB01533B8C1EF103065174F50BCA001</sign>
     * </xml>
     */
    private String buildParam(PayCommon requestParam) {

        /*构造参数map，便于之后生成签名，使用treemap为了获得排序集合，满足微信支付文档的要求*/
        Map<String, String> param = new TreeMap<>(String::compareToIgnoreCase);

        param.put("appid", wechatPayConfig.getAppId());
        param.put("mch_id", wechatPayConfig.getMchid());
        param.put("trade_type", getTradeType());
        param.put("nonce_str", this.createNoncestr());
        param.put("spbill_create_ip", requestParam.getRemoteIp());
        param.put("body", requestParam.getBody());
        param.put("out_trade_no", requestParam.getOutTradeNo());
        param.put("total_fee", String.valueOf(requestParam.getPayAmount().multiply(new BigDecimal(100)).intValue()));
        param.put("notify_url", requestParam.getNotifyUrl());
        param.put("sign", this.sign(param, wechatPayConfig.getApiKey()));

        /*构造XML请求参数*/
        StringBuilder builder = new StringBuilder();
        builder.append("<xml>");
        for (Map.Entry<String, String> item : param.entrySet()) {
            builder.append("<").append(item.getKey()).append(">");
            builder.append(item.getValue());
            builder.append("</").append(item.getKey()).append(">");
        }
        builder.append("</xml>");
        return builder.toString();
    }

    /**
     * 封装返回值
     *
     * @param xml 请求结果
     */
    protected abstract String buildResult(XMLUtils xml);


    /**
     * 获取请求结果
     *
     * @param requestResult 请求结果
     * @return
     */
    protected String getReult(String requestResult) {
        /*解析返回的结果，从结果中获取prepayid和noncestr*/
        XMLUtils xml = new XMLUtils(requestResult);
        Element root = xml.getRootElement();
        /*判断xml解析是否成功，root为null说明解析失败*/
        if (root == null) {
            throw new RuntimeException("");
        }
        /*判断返回的结果*/
        boolean isError = false;
        String returnCode = xml.getText(root, "return_code");
        String errCode = xml.getText(root, "err_code");
        /*返回错误信息*/
        String errCodeDes = "";
        if ("FAIL".equals(returnCode)) {
            isError = true;
            errCodeDes = xml.getText(root, "return_msg");
        } else if (!StringUtils.isEmpty(errCode)) {
            isError = true;
            errCodeDes = xml.getText(root, "err_code_des");
        }
        if (isError) {
            throw new RuntimeException(String.format("errCode:%s,errCodeDes:%s", errCode, errCodeDes));
        }
        return buildResult(xml);
    }


    /**
     * 支付接口
     *
     * @param payCommon 支付参数，不同实现类参数不同
     * @return
     */
    @Override
    public String pay(PayCommon payCommon) throws PayException {
        String param = buildParam(payCommon);
        RequestBo requestBo = new RequestBo();
        requestBo.setParam(param);
        requestBo.setProtocol(ProtocolEnum.https);

        RequestMethodEnum requestMethodEnum = RequestMethodEnum.of(wechatPayConfig.getRequestMethod());
        if (requestMethodEnum == null) {
            throw new RuntimeException("请求方法不存在，请查看微信支付的信息是否配置正确");
        }
        requestBo.setMethod(RequestMethodEnum.of(wechatPayConfig.getRequestMethod()));
        requestBo.setUrl(wechatPayConfig.getGatewayUrl());
        RespondBo respondBo = httpRequestService.doRequest(requestBo);
        return getReult((String) respondBo.getResult());
    }

    /**
     * 给第三方支付调用的回调方法
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param billSn   账单唯一标识，此处标识id
     * @return
     */
    @Override
    public NotifyDTO notifyCallBack(HttpServletRequest request, HttpServletResponse response, String billSn) {
        /*------------获取返回的参数------------*/
        Map<String, String> params = new TreeMap<>();
        for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
            params.put(param.getKey(), param.getValue()[0]);
        }

        /*------------校验签名------------*/
        /*获取返回的签名*/
        String retSign = params.get("sign");
        /*先把签名参数从参数集合中去除，为了计算签名*/
        params.remove("sign");
        /*计算签名*/
        String calSign = this.sign(params, wechatPayConfig.getApiKey());
        if (!calSign.equals(retSign)) {
            return NotifyDTO.fail();
        }

        /*------------校验返回的通信标记--------*/
        String returnCode = params.get("return_code");
        String returnMsg = params.get("return_msg");
        if (StringUtils.isEmpty(returnCode) || !returnCode.equalsIgnoreCase("success")) {
            log.error("payment:" + billSn + " " + returnMsg);
            return NotifyDTO.fail();
        }

        /*-------------校验返回的业务结果---------------*/
        String resultCode = params.get("result_code");
        String err = params.get("err_code_des");
        if (StringUtils.isEmpty(resultCode) || !resultCode.equalsIgnoreCase("success")) {
            log.error("payment:" + billSn + " " + err);
            return NotifyDTO.fail();
        }

        /*-------------校验返回的商户和appid---------------*/
        /*商户号*/
        String mchId = params.get("mch_id");
        /*公众账号id*/
        String appid = params.get("appid");
        /*微信移动支付的支付流水号*/
        String tradeNo = params.get("transaction_id");
        if (!StringUtils.isEmpty(mchId) && !StringUtils.isEmpty(appid) && appid.equals(wechatPayConfig.getAppId()) && mchId.equals(wechatPayConfig.getMchid())) {
            return new NotifyDTO(tradeNo, true, billSn);
        } else {
            return NotifyDTO.fail();
        }
    }

    /**
     * 退款接口
     *
     * @param refundCommon 退款的参数
     * @return
     */
    @Override
    public Boolean refund(RefundCommon refundCommon) {
        throw new RuntimeException("微信退款尚未实现");
    }

    /**
     * 退款的通知回调
     *
     * @param request
     * @param response
     * @param billSn   退款唯一标志
     * @return
     */
    @Override
    public NotifyDTO refundNotifyCallBack(HttpServletRequest request, HttpServletResponse response, String billSn) {
        return null;
    }

    /**
     * MD5签名
     *
     * @param content
     * @param key
     * @return
     */
    protected String sign(Map<String, String> content, String key) {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> item : content.entrySet()) {
            String k = item.getKey();
            String v = item.getValue();
            if (StringUtils.isEmpty(v)) {
                continue;
            }
            str.append(k).append("=").append(v).append("&");
        }
        str.append("key=").append(key);
        return XDigestUtils.md5Hex(str.toString()).toUpperCase();
    }

    /**
     * 商户生成的随机字符串 字符串类型，32个字节以下
     *
     * @return
     */
    protected String createNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res.append(chars.charAt(rd.nextInt(chars.length() - 1)));
        }
        return res.toString();
    }
}
