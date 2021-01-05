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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
@RequiredArgsConstructor
@Service
public class OrderAnalysisDataGenerateServiceImpl implements OrderAnalysisDataGenerateService {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(OrderAnalysisDataGenerateServiceImpl.class);

    private final SpotInformationApi spotInformationApi;

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
        AtomicInteger i = new AtomicInteger(0);
        currencySet.parallelStream().forEach(currency -> {
            forTag:
            try {
                int cursor = i.incrementAndGet();
                // limit值最大为1000，如为null则默认为500
                int limit = 1000;
                Long startId = null;
                List<BinanceOrder> binanceOrders;
                CurrencyPair currencyPair = new CurrencyPair(currency, BiConstants.BASE_CURRENCY);
                do {
                    binanceOrders = spotInformationApi.getTradeRecords(currencyPair.base, currencyPair.counter, limit, startId);
                    log.info("当前币：{}({}/{})，取到记录：【{}】条", currencyPair.toString(), cursor, currencySet.size(), binanceOrders.size());
                    if (CollectionUtils.isEmpty(binanceOrders)) {
                        break forTag;
                    }

                    // 过滤未完成订单
                    final List<BinanceOrder> filledOrders = binanceOrders.stream().filter(binanceOrder -> binanceOrder.status.equals(OrderStatus.FILLED)).collect(Collectors.toList());

                    // 循环时如果Map里Currency对应数据已经有列表数据，则添加新数据到旧数据列表中，如无则直接添加到Map中
                    List<BinanceOrder> orders = ordersMap.get(currencyPair);
                    if (null == orders) {
                        ordersMap.put(currencyPair, filledOrders);
                    } else {
                        orders.addAll(filledOrders);
                    }

                    // 当binanceOrders.size == limit时，重复拉取数据并设置startId
                    startId = binanceOrders.get(binanceOrders.size() - 1).orderId;
                } while (binanceOrders.size() == limit);
            } catch (BinanceException binanceException) {
                // 不存在的币，跳过
                if (!binanceException.getLocalizedMessage().contains("Invalid symbol")) {
                    log.error("调用币安发生异常", binanceException);
                }
            }
        });
        log.info("拉取订单分析数据结束，耗时：【{}】", System.currentTimeMillis() - now);
        return ordersMap;
    }
}
