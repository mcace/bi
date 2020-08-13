package com.mcsoft.bi.common.bian.collector;

import com.binance.client.model.market.AggregateTrade;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 币安接口服务
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
public interface ApiCollector {

    /**
     * 获取最近N秒内的聚合交易数据
     *
     * @param symbol  交易对
     * @param seconds 数据时间长度
     * @return 最近N秒内的聚合交易数据
     */
    public List<AggregateTrade> getRecentTradesBySeconds(String symbol, Integer seconds);

    /**
     * 获取聚合交易数据
     *
     * @param symbol 交易对
     * @param start  起始时间
     * @param end    结束时间
     * @return 聚合交易数据
     */
    public List<AggregateTrade> getAggregateTrades(String symbol, LocalDateTime start, LocalDateTime end);

}
