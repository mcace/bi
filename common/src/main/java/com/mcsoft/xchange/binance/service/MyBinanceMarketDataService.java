package com.mcsoft.xchange.binance.service;

import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.client.ResilienceRegistries;

/**
 * 自定义MarketDataService
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
public class MyBinanceMarketDataService extends BinanceMarketDataService {

    public MyBinanceMarketDataService(BinanceExchange exchange, BinanceAuthenticated binance, ResilienceRegistries resilienceRegistries) {
        super(exchange, binance, resilienceRegistries);
    }

}
