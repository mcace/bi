package com.mcsoft.bi.common.bian.spot.api;

import lombok.RequiredArgsConstructor;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.service.BinanceTradeService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Created by MC on 2020/12/4.
 *
 * @author MC
 */
@Service
@RequiredArgsConstructor
public class SpotInformationApiImpl implements SpotInformationApi {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(SpotInformationApiImpl.class);

    @Resource
    @Qualifier("binanceExchange")
    private Exchange binanceExchange;

    @Override
    public AccountInfo getAccountInformation() {
        final AccountService accountService = binanceExchange.getAccountService();
        return ExchangeHelper.coverIOException(accountService::getAccountInfo);
    }

    public List<BinanceOrder> getTradeRecords(Currency baseCurrency, Currency counterCurrency, Integer limit, Long startId) {
        final BinanceTradeService tradeService = (BinanceTradeService)binanceExchange.getTradeService();
        return ExchangeHelper.coverIOException(() -> tradeService.allOrders(new CurrencyPair(baseCurrency, counterCurrency), startId, limit));
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
