package com.mcsoft.xchange.binance;

import com.mcsoft.xchange.binance.service.MyBinanceAccountService;
import com.mcsoft.xchange.binance.service.MyBinanceMarketDataService;
import com.mcsoft.xchange.binance.service.MyBinanceTradeService;
import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.client.ExchangeRestProxyBuilder;

/**
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
public class MyBinanceExchange extends BinanceExchange {

    @Override
    protected void initServices() {
        super.initServices();
        BinanceAuthenticated binance = ExchangeRestProxyBuilder.forInterface(
                BinanceAuthenticated.class, getExchangeSpecification())
                .build();
        this.marketDataService = new MyBinanceMarketDataService(this, binance, getResilienceRegistries());
        this.tradeService = new MyBinanceTradeService(this, binance, getResilienceRegistries());
        this.accountService = new MyBinanceAccountService(this, binance, getResilienceRegistries());
    }
}
