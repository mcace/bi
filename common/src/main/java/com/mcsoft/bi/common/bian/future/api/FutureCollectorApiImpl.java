package com.mcsoft.bi.common.bian.future.api;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.impl.BinanceApiInternalFactory;
import com.binance.client.model.market.AggregateTrade;
import com.mcsoft.bi.common.bian.future.constants.ApiConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
public class FutureCollectorApiImpl implements FutureCollectorApi {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(FutureCollectorApiImpl.class);

    private final SyncRequestClient syncRequestClient;

    public FutureCollectorApiImpl(String apiKey, String apiSecret) {
        this.syncRequestClient = BinanceApiInternalFactory.getInstance().createSyncRequestClient(
                apiKey,
                apiSecret, new RequestOptions()
        );
    }

    @Override
    public List<AggregateTrade> getRecentAggregateTradesBySeconds(String symbol, Integer seconds) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.ofHours(8));
        LocalDateTime start = now.minusSeconds(seconds);
        return getAggregateTrades(symbol, start, now);
    }

    @Override
    public List<AggregateTrade> getAggregateTrades(String symbol, LocalDateTime start, LocalDateTime end) {
        List<AggregateTrade> result = new ArrayList<>(3000);

        // startTime,endTime时间
        final long endTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        final long startTime = start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

        List<AggregateTrade> aggregateTrades = null;
        Long fromId = null;
        // 循环处理未取完数据
        while (null == aggregateTrades || aggregateTrades.size() == ApiConstants.AGGREGATE_COUNT) {
            aggregateTrades = syncRequestClient.getAggregateTrades(symbol, fromId, startTime, endTime, ApiConstants.AGGREGATE_COUNT);
            if (CollectionUtils.isEmpty(aggregateTrades)) {
                break;
            }
            result.addAll(aggregateTrades);
            // 如未取完，则从最后一条继续取
            fromId = aggregateTrades.get(aggregateTrades.size() - 1).getId() + 1;
        }

        return result;
    }

}
