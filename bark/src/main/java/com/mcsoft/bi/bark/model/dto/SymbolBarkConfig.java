package com.mcsoft.bi.bark.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易对监控配置
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Data
public class SymbolBarkConfig {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 时间区间，从当前时间往前取多少秒
     */
    private Integer seconds;

    /**
     * 需要监控的价格变化百分比
     */
    private BigDecimal percent;

}
