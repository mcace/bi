package com.mcsoft.bi.common.model.bo;

import com.binance.client.model.market.AggregateTrade;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聚合价格区间分析结果
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Data
public class AggregatePricePeriodInfo {

    private LocalDateTime start;

    private LocalDateTime end;

    private AggregateTrade high;

    private AggregateTrade low;

    private AggregateTrade close;

    private AggregateTrade open;

}
