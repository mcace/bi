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

    private final DingBotService dingBotApi;
    private final ApiCollector apiCollector;
    private final SymbolBarkConfig config;
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
            // 规则1.结束点===当前点
            // 规则2.起始点根据情况决定
            // 情况1：极值2==当前点，则起始点=极值1
            if (tradesForSort.get(1).getPrice().equals(tradesForSort.get(2).getPrice())) {
                // 此时比对极值2与当前点是否价格一致，价格一致则起始点为极值1，不一致则起始点为极值2
                start = tradesForSort.get(0);
            }
            // 情况2：当前点在极值1-极值2之间，起始点为价格离当前点较远的那个
            // 判断当前点在极值1-极值2之间：(极值1-当前点)的绝对值+(极值2-当前点)的绝对值==(极值1-极值2)的绝对值
            // tips:情况1和情况2实际上是可以合并的
            else if (tradesForSort.get(0).getPrice().subtract(end.getPrice()).abs()
                    .add(tradesForSort.get(1).getPrice().subtract(end.getPrice()).abs())
                    .equals(tradesForSort.get(0).getPrice().subtract(tradesForSort.get(1).getPrice()).abs())) {
                // 判断价格离当前点较远的极值点：(极值1-当前点)的绝对值>(极值2-当前点)的绝对值，如大于则起始点为极值1，否则起始点为极值2
                start = tradesForSort.get(0).getPrice().subtract(end.getPrice()).abs()
                        .compareTo(tradesForSort.get(1).getPrice().subtract(end.getPrice()).abs()) > 0 ?
                        tradesForSort.get(0) : tradesForSort.get(1);
            }
            // 其他情况：起始点=极值2
            else {
                start = tradesForSort.get(1);
            }

            log.debug(getThreadName() + "计算百分比起始值：{}，结束值：{}", start, end);
            // 价格变化百分比
            BigDecimal percent = end.getPrice().subtract(start.getPrice())
                    .divide(start.getPrice(), 7, RoundingMode.HALF_UP).multiply(BigDecimal.TEN.pow(2));
            log.debug(getThreadName() + "价格变化百分比：" + percent.toString());
            // 检测价格变动是否超过了设定阈值
            boolean doNotice = percent.abs().compareTo(config.getPercent()) >= 0;
            try {
                if (doNotice) {
                    log.info(getThreadName() + "价格变化百分比：" + percent.toString() + "，准备进行通知");
                    String messageBuilder = config.getSymbol() + "最近" +
                            config.getSeconds() + "秒内价格变动达到" + percent.setScale(4, RoundingMode.HALF_UP) + "%，达到阈值：" + config.getPercent() + "。" +
                            "当前价格：" + end.getPrice() + "，" +
                            "上一极端价格：" + start.getPrice();
                    dingBotApi.sendMessageToBiGroup(messageBuilder);
                    // 如发出了通知，则至少等待一分钟后继续通知
                    int waitTime = Math.max(config.getSeconds() / 3, 60);
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
