package com.mcsoft.bi.bark.barker;

import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.DingBotService;
import com.mcsoft.bi.common.bian.collector.ApiCollector;

/**
 * Created by MC on 2020/8/13.
 *
 * @author MC
 */
public class Barker implements Runnable {

    private DingBotService dingBotApi;
    private ApiCollector apiCollector;
    private SymbolBarkConfig config;
    private volatile boolean running = true;

    public Barker(DingBotService dingBotService, ApiCollector apiCollector, SymbolBarkConfig config) {
        this.dingBotApi = dingBotService;
        this.apiCollector = apiCollector;
        this.config = config;
    }

    public String getThreadName() {
        return config.getSymbol() + "-" + config.getSeconds() + "-" + config.getPercent();
    }

    @Override
    public void run() {
        while (running) {
            // 核心逻辑

        }
    }

    public void shutdown() {
        this.running = false;
    }

}
