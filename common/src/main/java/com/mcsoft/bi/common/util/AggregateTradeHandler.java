package com.mcsoft.bi.common.util;

import com.binance.client.model.market.AggregateTrade;
import com.mcsoft.bi.common.exception.CollectionEmptyException;
import com.mcsoft.bi.common.model.bo.AggregatePricePeriodInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
public class AggregateTradeHandler {

    public static final Comparator<AggregateTrade> TIME_COMPARATOR =
            Comparator.comparingLong(AggregateTrade::getTime);


    public static AggregatePricePeriodInfo handle(List<AggregateTrade> trades) {
        if (CollectionUtils.isEmpty(trades)) {
            throw new CollectionEmptyException();
        }

        AggregatePricePeriodInfo info = new AggregatePricePeriodInfo();

        info.setOpen(trades.get(0));
        info.setClose(trades.get(trades.size() - 1));

        info.setStart(LocalDateTime.ofInstant(Instant.ofEpochMilli(info.getOpen().getTime()), ZoneOffset.ofHours(8)));
        info.setEnd(LocalDateTime.ofInstant(Instant.ofEpochMilli(info.getClose().getTime()), ZoneOffset.ofHours(8)));

        Comparator<AggregateTrade> comparator = Comparator.comparing(AggregateTrade::getPrice, BigDecimal::compareTo);
        // 找出最高/最低
        AggregateTrade high = trades.get(0);
        AggregateTrade low = trades.get(0);
        for (AggregateTrade trade : trades) {
            // TODO: 2020/8/11 问题：如果价格相同，则不会更新，此时最高/最低取的是第一个出现该值的点，会不会有问题？
            if (comparator.compare(trade, high) > 0) {
                high = trade;
            }
            if (comparator.compare(trade, low) < 0) {
                low = trade;
            }
        }
        info.setHigh(high);
        info.setLow(low);

        return info;
    }

    public static AggregateTrade getMostRecentTrade(AggregateTrade trade1, AggregateTrade trade2) {
        return TIME_COMPARATOR.compare(trade1, trade2) >= 0 ? trade1 : trade2;
    }

}
