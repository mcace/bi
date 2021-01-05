package com.mcsoft.bi.peeper.model.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.mcsoft.bi.peeper.util.excel.EnumConverter;
import com.mcsoft.bi.peeper.util.excel.TimestampConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.knowm.xchange.binance.dto.trade.OrderSide;

import java.math.BigDecimal;

/**
 * 订单Excel数据对象
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
@Data
@AllArgsConstructor
// 字符串的头背景设置成粉红 IndexedColors.PINK.getIndex()
@HeadStyle(horizontalAlignment = HorizontalAlignment.LEFT)
// 字符串的头字体设置成20
@HeadFontStyle(fontHeightInPoints = 12, fontName = "Hack", bold = false)
// 字符串的内容的背景设置成天蓝 IndexedColors.SKY_BLUE.getIndex()
// @ContentStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND, fillForegroundColor = 1)
// 字符串的内容字体设置成20
@ContentFontStyle(fontHeightInPoints = 12, fontName = "Hack")
public class BinanceOrderExcelDTO {

    @ExcelIgnore
    private String symbol;
    @ColumnWidth(28)
    @ExcelProperty(value = {"TIME"}, index = 0, converter = TimestampConverter.class)
    private long time;
    @ColumnWidth(22)
    @ExcelProperty(value = {"AMOUNT"}, index = 1)
    private BigDecimal executedQty;
    @ColumnWidth(22)
    @ExcelProperty(value = {"QTY"}, index = 2)
    private BigDecimal cummulativeQuoteQty;
    @ColumnWidth(13)
    @ExcelProperty(value = {"PRICE"}, index = 3)
    private BigDecimal price;
    @ColumnWidth(13)
    @ExcelProperty(value = {"BUY/SELL"}, index = 4, converter = EnumConverter.class)
    private OrderSide side;

}
