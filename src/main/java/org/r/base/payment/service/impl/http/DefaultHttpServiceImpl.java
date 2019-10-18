package org.r.base.payment.service.impl.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.r.base.payment.entity.RequestBo;
import org.r.base.payment.entity.RespondBo;
import org.r.base.payment.enums.ProtocolEnum;
import org.r.base.payment.enums.RequestMethodEnum;
import org.r.base.payment.service.HttpRequestService;
import org.r.base.payment.service.HttpRequestStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author casper
 * @date 19-10-16 下午3:02
 **/
@Service
@Slf4j
public class DefaultHttpServiceImpl implements HttpRequestService {


    @Autowired
    private PostHttpRequestStrategy postHttpRequestStrategy;


    /**
     * 获取请求处理单元
     *
     * @param requestMethod 请求方法
     * @return
     */
    @Override
    public HttpRequestStrategy getStrategy(RequestMethodEnum requestMethod) {
        HttpRequestStrategy tmp = null;
        switch (requestMethod) {
            case GET:
                break;
            case POST:
                tmp = postHttpRequestStrategy;
                break;
            case DELETE:
                break;
            case PUT:
                break;
            default:
                return null;
        }
        return tmp;
    }

    /**
     * 执行请求
     *
     * @param requestBo 请求信息
     * @return
     */
    @Override
    public RespondBo doRequest(RequestBo requestBo) {

        HttpRequestStrategy strategy = getStrategy(requestBo.getMethod());
        if (strategy == null) {
            throw new RuntimeException("不支持的请求方法");
        }
        OkHttpClient okHttpClient = buildClient(requestBo.getProtocol());
        Request request = strategy.buildRequest(requestBo);
        RespondBo respondBo = new RespondBo();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            ResponseBody body = execute.body();
            respondBo.setSuccess(execute.isSuccessful() && body != null);
            if (respondBo.getSuccess()) {
                respondBo.setResult(body.string());
            }
        } catch (IOException e) {
            e.printStackTrace();
            respondBo.setSuccess(false);
        }
        return respondBo;
    }


    /**
     * 根据协议构建请求客户端
     *
     * @param protocol 协议
     * @return 客户端
     */
    private OkHttpClient buildClient(ProtocolEnum protocol) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        switch (protocol) {
            default:
            case http:
                break;
            case https:
                try {
                    X509TrustManager trustManager = getTrustManager();
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{trustManager}, null);
                    builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    log.error("can not creat ssl context.Missing tls algorithm");
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    log.error("can not creat ssl context.KeyManagement off");
                }
                break;
        }
        return builder.build();
    }


    /**
     * 获取证书信任管理器
     *
     * @return
     */
    private X509TrustManager getTrustManager() {

        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }


}
