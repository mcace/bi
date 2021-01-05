package com.mcsoft.bi.peeper.service;

import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.List;
import java.util.Map;

/**
 * 订单分析数据生成Service
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
public interface OrderAnalysisDataGenerateService {

    Map<CurrencyPair, List<BinanceOrder>> generateOrderAnalysisData();

}
