package com.mcsoft.bi.common.bian.collector;

import com.binance.client.model.market.AggregateTrade;
import com.mcsoft.bi.bark.BarkApplication;
import com.mcsoft.bi.common.bian.constants.TokenConstants;
import com.mcsoft.bi.common.model.bo.AggregatePricePeriodInfo;
import com.mcsoft.bi.common.util.AggregateTradeHandler;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BarkApplication.class)
class ApiCollectorTest {

    @Autowired
    private ApiCollector apiCollector;

    @Test
    public void getRecentTradesBySeconds() {
        System.out.println("start:" + System.currentTimeMillis());
        final List<AggregateTrade> recentTradesBySeconds = apiCollector.getRecentAggregateTradesBySeconds(TokenConstants.BTC_USDT, 1800);
        System.out.println("end:" + System.currentTimeMillis());
        System.out.println(recentTradesBySeconds);
        final AggregatePricePeriodInfo handle = AggregateTradeHandler.handle(recentTradesBySeconds);
        System.out.println(handle);
    }

}