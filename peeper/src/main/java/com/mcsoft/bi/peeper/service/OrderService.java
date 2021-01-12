package com.mcsoft.bi.peeper.service;

import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.List;
import java.util.Map;

/**
 * 订单数据Service
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
public interface OrderService {

    /**
     * 拉取交易记录列表
     *
     * @param base    需要拉取的币
     * @param counter 拉取币的对手币，和baseCurrency组合成交易对，如ETH,USDT组合成ETHUSDT，作为接口参数symbol传出
     * @param startId 拉取起始交易订单id，可以为null，不为null时，从指定的交易订单id开始拉取，适用于增量拉取的情况
     * @return 交易记录列表
     */
    List<BinanceOrder> getOrders(Currency base, Currency counter, Long startId);

    /**
     * 生成全量交易记录列表
     *
     * @return 交易记录列表，key为交易对，value为交易记录列表
     */
    Map<CurrencyPair, List<BinanceOrder>> generateOrderAnalysisData();

}
