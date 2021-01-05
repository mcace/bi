package com.mcsoft.xchange.binance.service;

import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.service.BinanceTradeService;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.currency.CurrencyPair;

import java.io.IOException;
import java.util.List;

import static org.knowm.xchange.binance.BinanceResilience.REQUEST_WEIGHT_RATE_LIMITER;

/**
 * 自定义TradeService
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
public class MyBinanceTradeService extends BinanceTradeService {

    public MyBinanceTradeService(BinanceExchange exchange, BinanceAuthenticated binance, ResilienceRegistries resilienceRegistries) {
        super(exchange, binance, resilienceRegistries);
    }

    @Override
    public List<BinanceOrder> allOrders(CurrencyPair pair, Long orderId, Integer limit) throws BinanceException, IOException {
        return decorateApiCall(
                () ->
                        binance.allOrders(
                                BinanceAdapters.toSymbol(pair),
                                orderId,
                                limit,
                                getRecvWindow(),
                                getTimestampFactory(),
                                apiKey,
                                signatureCreator))
                .withRetry(retry("allOrders"))
                .withRateLimiter(rateLimiter(REQUEST_WEIGHT_RATE_LIMITER), 5)
                .call();
    }

}
