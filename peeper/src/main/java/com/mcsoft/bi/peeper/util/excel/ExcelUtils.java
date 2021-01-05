package com.mcsoft.bi.peeper.util.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.AllArgsConstructor;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
public class ExcelUtils {

    /**
     * 生成带多个sheet的表格
     *
     * @param closedAfterInvoke 传输流，调用后被关闭
     * @param dataList          sheet数据列表
     */
    public static void generateExcelWithSheets(OutputStream closedAfterInvoke, List<SheetInfo<?>> dataList) {
        ExcelWriter excelWriter = null;
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(closedAfterInvoke);
            excelWriter = EasyExcel.write(bufferedOutputStream).needHead(true).build();
            for (int i = 0; i < dataList.size(); i++) {
                final SheetInfo<?> sheetInfo = dataList.get(i);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetInfo.sheetName).head(sheetInfo.head).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();
                excelWriter.write(sheetInfo.data, writeSheet);
            }
        } finally {
            if (null != excelWriter) {
                excelWriter.finish();
            }
        }
    }

    @AllArgsConstructor
    public static class SheetInfo<T> {
        public final String sheetName;
        public final List<T> data;
        public final Class<T> head;
    }

}
