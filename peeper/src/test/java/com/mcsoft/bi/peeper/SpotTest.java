package com.mcsoft.bi.peeper;

import com.alibaba.fastjson.JSON;
import com.mcsoft.bi.common.bian.spot.api.SpotInformationApi;
import com.mcsoft.bi.common.util.TimeUtils;
import com.mcsoft.bi.peeper.constants.BiConstants;
import com.mcsoft.bi.peeper.model.dto.echarts.BuyPointDTO;
import com.mcsoft.bi.peeper.service.OrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.binance.dto.marketdata.BinanceKline;
import org.knowm.xchange.binance.dto.marketdata.BinancePriceQuantity;
import org.knowm.xchange.binance.dto.marketdata.KlineInterval;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.dto.trade.OrderSide;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

/**
 * Created by MC on 2021/1/7.
 *
 * @author MC
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SpotTest {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(SpotTest.class);

    @Autowired
    private SpotInformationApi spotInformationApi;
    @Autowired
    private OrderService orderService;

    @Test
    public void testBookTicker() {

        // 搬砖逻辑
        Integer i = 0;
        while (i < 20) {
            BinancePriceQuantity bookTicker = spotInformationApi.getBookTicker(Currency.ALGO, BiConstants.BASE_CURRENCY);
            BinancePriceQuantity bookTicker2 = spotInformationApi.getBookTicker(Currency.BUSD, BiConstants.BASE_CURRENCY);
            BinancePriceQuantity bookTicker3 = spotInformationApi.getBookTicker(Currency.ALGO, Currency.BUSD);

            BigDecimal balance = new BigDecimal("10");
            log.info("原本余额：{}", balance);
            BigDecimal algos = balance.divide(bookTicker.askPrice, 12, RoundingMode.HALF_UP);
            log.info("USDT买ALGO数量：{}", algos);
            BigDecimal busds = algos.multiply(bookTicker3.bidPrice);
            log.info("卖ALGO买BUSD数量：{}", busds);
            BigDecimal usdts = busds.multiply(bookTicker2.bidPrice);
            log.info("卖BUSD买USDT数量：{}", usdts);
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void testAllBookTicker() {

        // 搬砖逻辑
        List<BinancePriceQuantity> allBookTicker = spotInformationApi.getAllBookTicker();

        Currency aCoin = BiConstants.BASE_CURRENCY;
        // Currency bCoin = Currency.TUSD;
        AccountInfo accountInformation = spotInformationApi.getAccountInformation();
        Wallet wallet = accountInformation.getWallet();
        Set<Currency> currencies = wallet.getBalances().keySet();


        for (Currency bCoin : currencies) {
            currencies.parallelStream().forEach(middle -> {
                BinancePriceQuantity bookTicker = getBookTicker(allBookTicker, middle, aCoin);
                BinancePriceQuantity bookTicker2 = getBookTicker(allBookTicker, bCoin, aCoin);
                BinancePriceQuantity bookTicker3 = getBookTicker(allBookTicker, middle, bCoin);

                if (null == bookTicker || null == bookTicker2 || null == bookTicker3 ||
                        bookTicker.askPrice.compareTo(BigDecimal.ZERO) == 0 || bookTicker3.askPrice.compareTo(BigDecimal.ZERO) == 0) {
                    return;
                }

                try {
                    BigDecimal balance = new BigDecimal("1000");
                    BigDecimal algos = balance.divide(bookTicker.askPrice, 12, RoundingMode.HALF_UP);
                    BigDecimal busds = algos.multiply(bookTicker3.bidPrice);
                    BigDecimal usdts = busds.multiply(bookTicker2.bidPrice);
                    if (usdts.compareTo(balance) > 0) {
                        log.info("成了：middle：{}，bCoin：{}\n原本余额：{}\nUSDT买{}数量：{}\n卖{}买BUSD数量：{}\n卖BUSD买USDT数量：{}", middle, bCoin
                                , balance, middle.toString(), algos, middle.toString(), busds, usdts);
                    }
                } catch (Exception e) {
                    log.info("发生异常，bookTicker:{},bookTicker2:{},bookTicker3:{}", bookTicker, bookTicker2, bookTicker3);
                    throw e;
                }
            });
        }
    }

    private BinancePriceQuantity getBookTicker(List<BinancePriceQuantity> allBookTicker, Currency base, Currency counter) {
        return allBookTicker.stream().parallel().filter(q -> q.symbol.equals(base.getCurrencyCode() + counter.getCurrencyCode())).findAny().orElse(null);
    }

    @Test
    public void generateEchartsKLineData() {
        Currency base = Currency.getInstanceNoCreate("CRV");
        Currency counter = BiConstants.BASE_CURRENCY;
        List<BinanceKline> kline = spotInformationApi.getKline(base, counter, KlineInterval.h1, 1000, null, null);

        // 输出echarts需要的k线数据
        // 格式：数组嵌套： [[时间,开,收,低,高],[时间,开,收,低,高]]
        Object[][] objs = new Object[kline.size()][];
        for (int i = 0; i < kline.size(); i++) {
            BinanceKline binanceKline = kline.get(i);
            Object[] data = new Object[5];
            data[0] = TimeUtils.TimeFormat.YYYY_MM_DD_HH_MM_SS.formatMillisTime(binanceKline.getOpenTime());
            data[1] = binanceKline.getOpenPrice();
            data[2] = binanceKline.getClosePrice();
            data[3] = binanceKline.getLowPrice();
            data[4] = binanceKline.getHighPrice();
            objs[i] = data;
        }

        // 输出数组
        log.info("输出echarts数据：\n{}", JSON.toJSONString(objs));


        // 输出买点信息
        // 买点信息格式：
        /*
        [
            {
                name: 'XX标点',
                        coord: ['2013/5/31', 2300],
                value: 2300,
                        itemStyle: {
                            color: 'rgb(0,255,0)'
                        }
            }
         ]
         */
        List<BinanceOrder> orders = orderService.getOrders(base, counter, null);
        if (CollectionUtils.isNotEmpty(orders)) {
            BuyPointDTO[] buyPointDTOS = new BuyPointDTO[orders.size()];
            for (int i = 0; i < orders.size(); i++) {
                BinanceOrder binanceOrder = orders.get(i);

                BuyPointDTO buyPointDTO = new BuyPointDTO();
                buyPointDTO.setValue(binanceOrder.price.intValue());
                Object[] coord = new Object[2];

                // 按小时计，去除分秒，后续如果要改K线区间，该处也要跟着改
                LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(binanceOrder.time), ZoneOffset.ofHours(8)).withMinute(0).withSecond(0);

                coord[0] = TimeUtils.TimeFormat.YYYY_MM_DD_HH_MM_SS.formatLocalDateTime(time);
                coord[1] = binanceOrder.price;
                buyPointDTO.setCoord(coord);
                buyPointDTO.setName(binanceOrder.price.toPlainString());
                BuyPointDTO.ItemStyle itemStyle = new BuyPointDTO.ItemStyle();
                if (binanceOrder.side.equals(OrderSide.BUY)) {
                    itemStyle.setColor("rgb(135,206,250)");
                } else {
                    itemStyle.setColor("rgb(102,205,170)");
                }
                buyPointDTO.setItemStyle(itemStyle);

                buyPointDTOS[i] = buyPointDTO;
            }

            log.info("输出echarts买点数据：\n{}", JSON.toJSONString(buyPointDTOS));
        }

    }

}
