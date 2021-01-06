package com.mcsoft.bi.peeper;

import com.mcsoft.bi.common.bian.spot.api.SpotInformationApi;
import com.mcsoft.bi.peeper.constants.BiConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.binance.dto.marketdata.BinancePriceQuantity;
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

}
