package com.mcsoft.bi.peeper.util.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.mcsoft.bi.common.util.TimeUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 时间戳转时间字符串yyyy-MM-dd HH:mm:ss
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
public class TimestampConverter implements Converter<Long> {

    @Override
    public Class<Long> supportJavaTypeKey() {
        return Long.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Long convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String cellDataStringValue = cellData.getStringValue();
        return TimeUtils.TimeFormat.YYYY_MM_DD_HH_MM_SS.parseLocalDateTime(cellDataStringValue).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    @Override
    public CellData<String> convertToExcelData(Long value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData<>(TimeUtils.TimeFormat.YYYY_MM_DD_HH_MM_SS.formatLocalDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.ofHours(8))));
    }
}
