package com.mcsoft.bi.peeper;

import com.alibaba.fastjson.JSON;
import com.binance.client.model.market.MarkPrice;
import com.mcsoft.bi.common.bian.future.api.FutureInformationApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by MC on 2021/1/13.
 *
 * @author MC
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class FutureTest {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(FutureTest.class);

    @Autowired
    private FutureInformationApi futureInformationApi;

    @Test
    public void getMarkPrices() {
        // 拉取标记价格及资金费率列表，选出较高的资金费率列表

        List<MarkPrice> markPrice = futureInformationApi.getMarkPrice(null);
        List<MarkPrice> sorted = markPrice.stream().sorted(Comparator.comparing(MarkPrice::getLastFundingRate).reversed()).collect(Collectors.toList());
        log.info("输出资金费率列表：{}", JSON.toJSONString(sorted));

    }

}
