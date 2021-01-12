package com.mcsoft.bi.common.bian.spot.api;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.BinancePrice;
import org.knowm.xchange.binance.dto.marketdata.BinancePriceQuantity;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.service.BinanceAccountService;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.binance.service.BinanceTradeService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Created by MC on 2020/12/4.
 *
 * @author MC
 */
@Service
public class SpotInformationApiImpl implements SpotInformationApi {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(SpotInformationApiImpl.class);

    private final Exchange binanceExchange;

    private final BinanceTradeService binanceTradeService;
    private final BinanceMarketDataService binanceMarketDataService;
    private final BinanceAccountService binanceAccountService;

    public SpotInformationApiImpl(@Qualifier("binanceExchange") Exchange binanceExchange) {
        this.binanceExchange = binanceExchange;
        this.binanceAccountService = (BinanceAccountService)binanceExchange.getAccountService();
        this.binanceTradeService = (BinanceTradeService)binanceExchange.getTradeService();
        this.binanceMarketDataService = (BinanceMarketDataService)binanceExchange.getMarketDataService();
    }

    @Override
    public AccountInfo getAccountInformation() {
        return ExchangeHelper.coverIOException(binanceAccountService::getAccountInfo);
    }

    public List<BinanceOrder> getTradeRecords(Currency base, Currency counter, Integer limit, Long startId) {
        return ExchangeHelper.coverIOException(() -> binanceTradeService.allOrders(new CurrencyPair(base, counter), startId, limit));
    }

    @Override
    public BinancePrice getPriceTicker(Currency base, Currency counter) {
        CurrencyPair symbol = new CurrencyPair(base, counter);
        return ExchangeHelper.coverIOException(() -> binanceMarketDataService.tickerPrice(symbol));
    }

    @Override
    public BinancePriceQuantity getBookTicker(Currency base, Currency counter) {
        List<BinancePriceQuantity> binancePriceQuantities = ExchangeHelper.coverIOException(binanceMarketDataService::tickerAllBookTickers);
        if (null == binancePriceQuantities) {
            return null;
        }
        return binancePriceQuantities.stream().parallel().filter(q -> q.symbol.equals(base.getCurrencyCode() + counter.getCurrencyCode())).findAny().orElse(null);
    }

    @Override
    public List<BinancePriceQuantity> getAllBookTicker() {
        return ExchangeHelper.coverIOException(binanceMarketDataService::tickerAllBookTickers);
    }

    @Override
    public List<BinanceKline> getKline(Currency base, Currency counter, KlineInterval interval, Integer limit, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        CurrencyPair pair = new CurrencyPair(base, counter);
        Long startTime = null == startDateTime ? null : startDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        Long endTime = null == endDateTime ? null : endDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return ExchangeHelper.coverIOException(() -> binanceMarketDataService.klines(pair, interval, limit, startTime, endTime));
    }

    public static class ExchangeHelper {

        public static <T> T coverIOException(IOExceptionSupplier<T> supplier) {
            try {
                return supplier.get();
            } catch (IOException e) {
                log.error("IO发生异常", e);
            }
            return null;
        }

    }

    @FunctionalInterface
    public interface IOExceptionSupplier<T> {
        T get() throws IOException;
    }

}
