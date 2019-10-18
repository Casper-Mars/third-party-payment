package org.r.base.payment.service.impl.http;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.r.base.payment.entity.RequestBo;
import org.r.base.payment.service.HttpRequestStrategy;
import org.springframework.stereotype.Service;

/**
 * @author casper
 * @date 19-10-16 下午2:44
 **/
@Service
public class PostHttpRequestStrategy implements HttpRequestStrategy {


    /**
     * 执行请求
     *
     * @param requestBo 请求信息
     * @return
     */
    @Override
    public Request buildRequest(RequestBo requestBo) {
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml"), (String) requestBo.getParam());
        builder.url(requestBo.getUrl())
                .post(requestBody);
        return builder.build();
    }
}
