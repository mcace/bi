package com.mcsoft.bi.bark.model.dto;

import lombok.Data;

import java.util.Set;

/**
 * 提示币价的配置项
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Data
public class BarkConfigs {

    private Set<SymbolBarkConfig> symbolBarkConfigs;

}
