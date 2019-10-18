package org.r.base.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author zj
 * @date 2019/3/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyDTO {

    /**
     * 支付流水号
     */
    private String tradeNo;
    /**
     * 支付状态
     */
    private boolean success;

    /**
     * 账单唯一标识
     */
    private String outTradeNo;

    public static NotifyDTO fail() {
        return new NotifyDTO(null, false, null);
    }


}
