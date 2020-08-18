package com.mcsoft.bi.bark.service;

import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;

import java.util.Set;

/**
 * 监控配置项Service
 * Created by MC on 2020/8/18.
 *
 * @author MC
 */
public interface BarkConfigService {

    /**
     * 列出所有配置
     *
     * @return 所有配置
     */
    public Set<SymbolBarkConfig> list();

    /**
     * 增加配置
     *
     * @param config 配置
     */
    public void add(SymbolBarkConfig config);

    /**
     * 删除配置
     *
     * @param config 配置项
     */
    public void remove(SymbolBarkConfig config);

}
