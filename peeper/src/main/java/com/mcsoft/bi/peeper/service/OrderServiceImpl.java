package com.mcsoft.bi.peeper.service;

import com.mcsoft.bi.common.bian.spot.api.SpotInformationApi;
import com.mcsoft.bi.peeper.constants.BiConstants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.dto.trade.OrderStatus;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final SpotInformationApi spotInformationApi;

    @Override
    public List<BinanceOrder> getOrders(Currency base, Currency counter, Long startId) {
        List<BinanceOrder> binanceOrders = new ArrayList<>();
        try {
            // limit值最大为1000，如为null则默认为500
            int limit = 1000;
            CurrencyPair currencyPair = new CurrencyPair(base, counter);
            do {
                List<BinanceOrder> orders = spotInformationApi.getTradeRecords(currencyPair.base, currencyPair.counter, limit, startId);
                if (CollectionUtils.isEmpty(orders)) {
                    return binanceOrders;
                }

                // 过滤未完成订单
                final List<BinanceOrder> filledOrders = orders.stream().filter(binanceOrder -> binanceOrder.status.equals(OrderStatus.FILLED)).collect(Collectors.toList());

                binanceOrders.addAll(filledOrders);

                // 当binanceOrders.size == limit时，重设startId为最后一条数据的订单id并继续拉取数据
                startId = binanceOrders.get(binanceOrders.size() - 1).orderId;
            } while (binanceOrders.size() == limit);
        } catch (BinanceException binanceException) {
            // 不存在的币，跳过
            if (!binanceException.getLocalizedMessage().contains("Invalid symbol")) {
                log.error("调用币安发生异常", binanceException);
            }
        }
        return binanceOrders;
    }

    @Override
    public Map<CurrencyPair, List<BinanceOrder>> generateOrderAnalysisData() {
        log.info("拉取订单分析数据");
        long now = System.currentTimeMillis();
        // 拉取账户信息
        final AccountInfo accountInfo = spotInformationApi.getAccountInformation();
        // 拉取所有币交易记录
        final Wallet wallet = accountInfo.getWallet();
        if (null == wallet) {
            return null;
        }
        final Map<Currency, Balance> balances = wallet.getBalances();

        final Set<Currency> currencySet = balances.keySet();
        Map<CurrencyPair, List<BinanceOrder>> ordersMap = new HashMap<>();

        AtomicInteger cursor = new AtomicInteger(0);
        Currency baseCurrency = BiConstants.BASE_CURRENCY;

        currencySet.parallelStream().forEach(currency -> {
            List<BinanceOrder> orders = getOrders(currency, baseCurrency, null);
            CurrencyPair currencyPair = new CurrencyPair(currency, baseCurrency);
            log.info("当前币：{}({}/{})，取到记录：【{}】条", currencyPair.toString(), cursor.incrementAndGet(), currencySet.size(), orders.size());
            if (CollectionUtils.isNotEmpty(orders)) {
                ordersMap.put(currencyPair, orders);
            }
        });
        log.info("拉取订单分析数据结束，耗时：【{}】", System.currentTimeMillis() - now);
        return ordersMap;
    }
}
