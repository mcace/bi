package com.mcsoft.bi.peeper.model.dto.echarts;

import lombok.Data;

/**
 * Created by MC on 2021/1/12.
 *
 * @author MC
 */
@Data
public class BuyPointDTO {

    private String name;
    private Integer value;
    private ItemStyle itemStyle;
    /**
     * 坐标，格式：[时间,数值]
     */
    private Object[] coord;

    @Data
    public static class ItemStyle {
        private String color;
    }
}
