package com.mcsoft.bi.common.bian.future.api;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.impl.BinanceApiInternalFactory;
import com.binance.client.model.market.MarkPrice;
import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Created by MC on 2020/11/27.
 *
 * @author MC
 */
public class FutureInformationApiImpl implements FutureInformationApi {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(FutureInformationApiImpl.class);

    private final SyncRequestClient syncRequestClient;

    public FutureInformationApiImpl(String apiKey, String apiSecret) {
        this.syncRequestClient = BinanceApiInternalFactory.getInstance().createSyncRequestClient(
                apiKey,
                apiSecret, new RequestOptions()
        );
    }

    @Override
    public AccountInformation getAccountInformation() {
        return syncRequestClient.getAccountInformation();
    }

    @Override
    public List<Order> getAllOrders(String symbol, Long orderId, LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        final Long startTimeLong = startTime == null ? null : startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        final Long endTimeLong = endTime == null ? null : endTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return syncRequestClient.getAllOrders(symbol, orderId, startTimeLong, endTimeLong, limit);
    }

    @Override
    public List<MarkPrice> getMarkPrice(String symbol) {
        return syncRequestClient.getMarkPrice(symbol);
    }

}
