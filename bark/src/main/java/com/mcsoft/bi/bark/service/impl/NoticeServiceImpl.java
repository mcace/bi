package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.DingBotService;
import com.mcsoft.bi.bark.service.NoticeService;
import com.mcsoft.bi.common.bian.collector.ApiCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private DingBotService dingBotService;
    @Autowired
    private ApiCollector apiCollector;

    @Override
    public void start() {
        final BarkConfigs barkConfigs = AppContext.getInstance().getBarkConfigs();

        final Set<SymbolBarkConfig> symbolBarkConfigs = barkConfigs.getSymbolBarkConfigs();

        // TODO: 2020/8/12 分别启监控服务
        for (SymbolBarkConfig symbolBarkConfig : symbolBarkConfigs) {
            startNewBark(symbolBarkConfig);
        }
    }

    @Override
    public void startNewBark(SymbolBarkConfig symbolBarkConfig) {


    }

}
