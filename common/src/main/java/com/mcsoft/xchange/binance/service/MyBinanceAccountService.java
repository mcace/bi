package com.mcsoft.xchange.binance.service;

import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.service.BinanceAccountService;
import org.knowm.xchange.client.ResilienceRegistries;

/**
 * 自定义AccountService
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
public class MyBinanceAccountService extends BinanceAccountService {
    public MyBinanceAccountService(BinanceExchange exchange, BinanceAuthenticated binance, ResilienceRegistries resilienceRegistries) {
        super(exchange, binance, resilienceRegistries);
    }
}
