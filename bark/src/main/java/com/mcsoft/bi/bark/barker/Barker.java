package com.mcsoft.bi.bark.barker;

import com.binance.client.model.market.AggregateTrade;
import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.DingBotService;
import com.mcsoft.bi.common.bian.collector.ApiCollector;
import com.mcsoft.bi.common.model.bo.AggregatePricePeriodInfo;
import com.mcsoft.bi.common.util.AggregateTradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by MC on 2020/8/13.
 *
 * @author MC
 */
public class Barker implements Runnable {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(Barker.class);

    private DingBotService dingBotApi;
    private ApiCollector apiCollector;
    private SymbolBarkConfig config;
    private volatile boolean running = true;

    public Barker(DingBotService dingBotService, ApiCollector apiCollector, SymbolBarkConfig config) {
        this.dingBotApi = dingBotService;
        this.apiCollector = apiCollector;
        this.config = config;
    }

    public String getThreadName() {
        return config.getSymbol() + "-" + config.getSeconds() + "-" + config.getPercent();
    }

    @Override
    public void run() {
        while (running) {
            // 核心逻辑
            // 取出最近交易数据
            List<AggregateTrade> trades = apiCollector.getRecentAggregateTradesBySeconds(config.getSymbol(), config.getSeconds());
            AggregatePricePeriodInfo pricePeriodInfo = AggregateTradeHandler.handle(trades);
            log.debug(getThreadName() + "最近交易数据：" + pricePeriodInfo);
            // 计算最大值、最小值与当前点的关系
            // 价格变化百分比统计结束点，固定为当前点
            AggregateTrade end = pricePeriodInfo.getClose();
            // 价格变化百分比统计起始点
            AggregateTrade start;
            // 起始点为当前点的最近极值点，如最近极值是当前点，则再向前取一个极值点
            List<AggregateTrade> tradesForSort = Arrays.asList(pricePeriodInfo.getHigh(), pricePeriodInfo.getLow(), end);
            tradesForSort.sort(AggregateTradeHandler.TIME_COMPARATOR);
            // 经过按时间排序后，点顺序必然为：极值1，极值2，当前点
            // 此时比对极值2与当前点是否价格一致，价格一致则起始点为极值1，不一致则起始点为极值2
            if (tradesForSort.get(1).getPrice().equals(tradesForSort.get(2).getPrice())) {
                start = tradesForSort.get(0);
            } else {
                start = tradesForSort.get(1);
            }

            log.debug(getThreadName() + "计算百分比起始值：{}，结束值：{}", start, end);
            // 价格变化百分比
            BigDecimal percent = start.getPrice().subtract(end.getPrice())
                    .divide(start.getPrice(), 5, RoundingMode.HALF_UP).multiply(BigDecimal.TEN.pow(2));
            log.debug(getThreadName() + "价格变化百分比：" + percent.toString());
            // 检测价格变动是否超过了设定阈值
            boolean doNotice = percent.abs().compareTo(config.getPercent()) >= 0;
            try {
                if (doNotice) {
                    String messageBuilder = config.getSymbol() + "最近" +
                            config.getSeconds() + "秒内价格变动达到" + percent + "%。" +
                            "当前价格：" + pricePeriodInfo.getClose().getPrice();
                    dingBotApi.sendMessageToBiGroup(messageBuilder);
                    // 如发出了通知，则至少等待一分钟后继续通知
                    Integer waitTime = Math.max(config.getSeconds() / 3, 60);
                    Thread.sleep(TimeUnit.SECONDS.toMillis(waitTime));
                } else {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(AppContext.currentContext().getAppConfig().getDuration()));
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }

}
