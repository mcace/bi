package com.mcsoft.bi.peeper.job;

import com.mcsoft.bi.peeper.model.dto.BinanceOrderExcelDTO;
import com.mcsoft.bi.peeper.service.OrderAnalysisDataGenerateService;
import com.mcsoft.bi.peeper.util.excel.ExcelUtils;
import org.apache.commons.collections4.MapUtils;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
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
    private OrderAnalysisDataGenerateService orderAnalysisDataGenerateService;

    @Value("${tmp-file-path}")
    private String filePath;

    public void export() throws FileNotFoundException {
        Map<CurrencyPair, List<BinanceOrder>> currencyListMap = orderAnalysisDataGenerateService.generateOrderAnalysisData();
        if (MapUtils.isEmpty(currencyListMap)) {
            log.info("无订单数据，结束处理");
            return;
        }

        log.info("拉取到订单数据，准备转换为DTO输出excel");
        String fileName = new Date().getTime() + "_bi.xlsx";
        String pathname = filePath + File.separator + fileName;
        File file = new File(pathname);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        List<ExcelUtils.SheetInfo<?>> sheetInfoList = new ArrayList<>(currencyListMap.size());
        // 输出Excel
        for (Map.Entry<CurrencyPair, List<BinanceOrder>> orderEntry : currencyListMap.entrySet()) {
            String symbol = orderEntry.getKey().toString().replace("/", "");
            List<BinanceOrderExcelDTO> orderExcelDTOS = orderEntry.getValue().stream()
                    .map(order -> new BinanceOrderExcelDTO(order.symbol, order.time, order.executedQty, order.cummulativeQuoteQty, order.price, order.side))
                    .collect(Collectors.toList());
            ExcelUtils.SheetInfo<BinanceOrderExcelDTO> sheetInfo = new ExcelUtils.SheetInfo<>(symbol, orderExcelDTOS, BinanceOrderExcelDTO.class);
            sheetInfoList.add(sheetInfo);
        }
        log.info("DTO转换完成，输出excel到：{}", pathname);
        ExcelUtils.generateExcelWithSheets(fileOutputStream, sheetInfoList);
    }

}
