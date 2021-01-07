package com.mcsoft.bi.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
public abstract class TimeUtils {

    public enum TimeFormat {
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), YYYY_MM_DD("yyyy-MM-dd"), YYYYMMDDHHMMSS("yyyyMMddHHmmss");

        private final String format;
        private final DateTimeFormatter formatter;

        TimeFormat(String format) {
            this.format = format;
            this.formatter = DateTimeFormatter.ofPattern(format);
        }

        public String formatLocalDateTime(LocalDateTime time) {
            return formatter.format(time);
        }

        public LocalDateTime parseLocalDateTime(String time) {
            return LocalDateTime.from(formatter.parse(time));
        }

        public String formatNow() {
            return formatter.format(LocalDateTime.now());
        }
    }

}
