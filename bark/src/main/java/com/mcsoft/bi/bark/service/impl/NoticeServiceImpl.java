package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.barker.Barker;
import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.DingBotService;
import com.mcsoft.bi.bark.service.NoticeService;
import com.mcsoft.bi.common.bian.collector.ApiCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
            Runtime.getRuntime().availableProcessors() * 2, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(16),
            r -> {
                if (r instanceof Barker) {
                    return new Thread(r, ((Barker)r).getThreadName());
                }
                return new Thread(r);
            });

    @Override
    public void start() {
        final BarkConfigs barkConfigs = AppContext.currentContext().getBarkConfigs();

        final Set<SymbolBarkConfig> symbolBarkConfigs = barkConfigs.getSymbolBarkConfigs();

        // 分别启动监控服务
        for (SymbolBarkConfig symbolBarkConfig : symbolBarkConfigs) {
            startNewBark(symbolBarkConfig);
        }
    }

    @Override
    public void startNewBark(SymbolBarkConfig symbolBarkConfig) {
        AppContext.currentContext().computeBarkerIfAbsent(symbolBarkConfig, config -> {
            Barker barker = new Barker(dingBotService, apiCollector, config);
            executor.execute(barker);
            return barker;
        });
    }

    @Override
    public void removeBark(SymbolBarkConfig symbolBarkConfig) {
        final Barker barker = AppContext.currentContext().removeBarker(symbolBarkConfig);
        barker.shutdown();
    }

}
