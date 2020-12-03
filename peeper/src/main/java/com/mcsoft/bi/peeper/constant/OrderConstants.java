package com.mcsoft.bi.peeper.constant;

import java.time.LocalDateTime;

/**
 * 订单相关常量
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
public interface OrderConstants {

    /**
     * 固定查询起始时间为2020-09-01 00:00:00
     */
    LocalDateTime QUERY_START_TIME = LocalDateTime.of(2020, 9, 1, 0, 0, 0, 0);

    /**
     * 默认交易对手币
     */
    String DEFAULT_TRADE_COIN = "USDT";

}
