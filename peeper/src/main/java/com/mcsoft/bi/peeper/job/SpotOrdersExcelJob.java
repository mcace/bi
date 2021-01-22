package com.mcsoft.bi.peeper.job;

import com.mcsoft.bi.common.bian.spot.api.SpotInformationApi;
import com.mcsoft.bi.common.util.TimeUtils;
import com.mcsoft.bi.peeper.model.dto.BinanceOrderExcelDTO;
import com.mcsoft.bi.peeper.service.OrderService;
import com.mcsoft.bi.peeper.util.excel.ExcelUtils;
import org.apache.commons.collections4.MapUtils;
import org.knowm.xchange.binance.dto.marketdata.BinancePrice;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.dto.trade.OrderSide;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
@Service
public class SpotOrdersExcelJob {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(SpotOrdersExcelJob.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private SpotInformationApi spotInformationApi;

    @Value("${tmp-file-path}")
    private String filePath;

    public void export() throws FileNotFoundException {
        long now = System.currentTimeMillis();
        log.info("开始导出Excel，拉取订单数据");
        Map<CurrencyPair, List<BinanceOrder>> currencyListMap = orderService.generateOrderAnalysisData();
        if (MapUtils.isEmpty(currencyListMap)) {
            log.info("无订单数据，结束处理");
            return;
        }

        log.info("拉取到订单数据{}条，准备转换为DTO输出excel", currencyListMap.size());
        // Excel文件信息
        String fileName = TimeUtils.TimeFormat.YYYYMMDDHHMMSS.formatNow() + "_bi.xlsx";
        String pathname = filePath + File.separator + fileName;
        File file = new File(pathname);
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        // Excel文件数据
        List<ExcelUtils.SheetInfo<?>> sheetInfoList = new ArrayList<>(currencyListMap.size());

        // 获取钱包余额，用于计算当前币余额
        AccountInfo accountInformation = spotInformationApi.getAccountInformation();
        Wallet wallet = accountInformation.getWallet();
        // 输出Excel
        for (Map.Entry<CurrencyPair, List<BinanceOrder>> orderEntry : currencyListMap.entrySet()) {

            List<BinanceOrderExcelDTO> orderExcelDTOS = orderEntry.getValue().stream()
                    .map(order -> new BinanceOrderExcelDTO(order.symbol, order.time, order.executedQty, order.cummulativeQuoteQty, order.price, order.side))
                    .collect(Collectors.toList());

            // 生成当前余额
            CurrencyPair pair = orderEntry.getKey();
            ExcelUtils.SheetInfo<BinanceOrderExcelDTO> sheetInfo = getCurrentBalanceSheetInfo(wallet, orderExcelDTOS, pair);

            sheetInfoList.add(sheetInfo);
        }

        // 按持仓量（等值U数量）排列
        sheetInfoList.sort(Comparator.comparing((headSheetInfo) -> {
            List<BinanceOrderExcelDTO> data = ((ExcelUtils.SheetInfo<BinanceOrderExcelDTO>)headSheetInfo).data;
            BinanceOrderExcelDTO currentBalance = data.get(data.size() - 1);
            return currentBalance.getCummulativeQuoteQty();
        }).reversed());

        log.info("DTO转换完成，输出excel到：{}", pathname);
        ExcelUtils.generateExcelWithSheets(fileOutputStream, sheetInfoList);
        log.info("导出Excel完成，耗时：【{}】", System.currentTimeMillis() - now);
    }

    private ExcelUtils.SheetInfo<BinanceOrderExcelDTO> getCurrentBalanceSheetInfo(Wallet wallet, List<BinanceOrderExcelDTO> orderExcelDTOS, CurrencyPair pair) {
        Currency base = pair.base;
        Currency counter = pair.counter;
        String symbol = base.getCurrencyCode() + counter.getCurrencyCode();
        Balance balance = wallet.getBalance(base);
        BinancePrice symbolPriceTicker = spotInformationApi.getPriceTicker(base, counter);
        BigDecimal currentPairPrice = symbolPriceTicker.getPrice();
        BigDecimal currentPairBalance = BigDecimal.ZERO;
        // 如余额不为0，则计算当前交易对的余额
        if (balance.getAvailable().compareTo(BigDecimal.ZERO) > 0) {
            currentPairBalance = balance.getAvailable().multiply(currentPairPrice);
        }
        log.info("获取到【{}】钱包余额为:【{}】，当前交易对【{}】价格为：【{}】，计算交易对余额为：【{}】", base, symbol, balance.getAvailable().toPlainString(), currentPairPrice, currentPairBalance);
        orderExcelDTOS.add(new BinanceOrderExcelDTO(symbol, System.currentTimeMillis(), balance.getAvailable(), currentPairBalance, currentPairPrice, OrderSide.SELL));
        return new ExcelUtils.SheetInfo<>(symbol, orderExcelDTOS, BinanceOrderExcelDTO.class);
    }

}
