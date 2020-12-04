package com.mcsoft.bi.peeper.service;

import com.mcsoft.bi.common.bian.spot.api.SpotInformationApi;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.dto.trade.OrderStatus;
import org.knowm.xchange.currency.Currency;
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
    public Map<Currency, List<BinanceOrder>> generateOrderAnalysisData() {
        // 拉取账户信息
        final AccountInfo accountInfo = spotInformationApi.getAccountInformation();
        // 拉取所有币交易记录
        final Wallet wallet = accountInfo.getWallet();
        if (null == wallet) {
            return null;
        }
        final Map<Currency, Balance> balances = wallet.getBalances();

        final Set<Currency> currencySet = balances.keySet();
        Map<Currency, List<BinanceOrder>> ordersMap = new HashMap<>();
        for (Currency currency : currencySet) {
            forTag:
            try {
                // limit值最大为1000，如为null则默认为500
                int limit = 1000;
                Long startId = null;
                List<BinanceOrder> binanceOrders;
                do {
                    binanceOrders = spotInformationApi.getTradeRecords(currency, Currency.USDT, limit, startId);
                    if (CollectionUtils.isEmpty(binanceOrders)) {
                        break forTag;
                    }

                    // 过滤未完成订单
                    final List<BinanceOrder> filledOrders = binanceOrders.stream().filter(binanceOrder -> binanceOrder.status.equals(OrderStatus.FILLED)).collect(Collectors.toList());

                    // 循环时如果Map里Currency对应数据已经有列表数据，则添加新数据到旧数据列表中，如无则直接添加到Map中
                    List<BinanceOrder> orders = ordersMap.get(currency);
                    if (null == orders) {
                        ordersMap.put(currency, filledOrders);
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
        }
        return ordersMap;
    }
}
