package com.mcsoft.bi.peeper.service;

import com.mcsoft.bi.common.bian.spot.api.SpotInformationApi;
import com.mcsoft.bi.peeper.constants.BiConstants;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.binance.dto.marketdata.BinancePrice;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by MC on 2020/12/4.
 *
 * @author MC
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SpotInformationApi spotInformationApi;

    @Test
    void generateOrderAnalysisData() {
        Map<CurrencyPair, List<BinanceOrder>> currencyListMap = orderService.generateOrderAnalysisData();
        Map<String, List<BinanceOrder>> map = currencyListMap.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue()));
    }

    @Test
    public void getSymbolPriceTicker() {
        BinancePrice symbolPriceTicker = spotInformationApi.getPriceTicker(Currency.ALGO, BiConstants.BASE_CURRENCY);
        System.out.println(symbolPriceTicker);
    }

}