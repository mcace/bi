package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.barker.Barker;
import com.mcsoft.bi.bark.constants.ThreadPoolConstants;
import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.DingBotService;
import com.mcsoft.bi.bark.service.NoticeService;
import com.mcsoft.bi.common.bian.api.ApiCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Autowired
    private DingBotService dingBotService;
    @Autowired
    private ApiCollector apiCollector;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
            ThreadPoolConstants.MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128),
            r -> {
                if (r instanceof Barker) {
                    return new Thread(r, ((Barker)r).getThreadName());
                }
                return new Thread(r);
            }) {
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            // 异常处理
            if (r instanceof Barker) {
                log.error(((Barker)r).getThreadName() + "出现异常", t);
            } else {
                log.error("线程异常", t);
            }
        }
    };

    @Override
    public void start() {
        final BarkConfigs barkConfigs = AppContext.currentContext().getBarkConfigs();
        log.info("获取BarkConfigs：" + barkConfigs);

        final Set<SymbolBarkConfig> symbolBarkConfigs = barkConfigs.getSymbolBarkConfigs();

        // 分别启动监控服务
        for (SymbolBarkConfig symbolBarkConfig : symbolBarkConfigs) {
            startNewBark(symbolBarkConfig);
        }
    }

    @Override
    public void startNewBark(SymbolBarkConfig symbolBarkConfig) {
        AppContext.currentContext().computeBarkerIfAbsent(symbolBarkConfig, config -> {
            try {
                log.info("准备启动barker：" + config);
                Barker barker = new Barker(dingBotService, apiCollector, config);
                executor.execute(barker);
                log.info("barker：" + config + "启动完成");
                return barker;
            } catch (Exception ex) {
                log.error("启动barker" + config + "失败", ex);
                throw ex;
            }
        });
    }

    @Override
    public void removeBark(SymbolBarkConfig symbolBarkConfig) {
        log.info("准备移除barker：" + symbolBarkConfig);
        final Barker barker = AppContext.currentContext().removeBarker(symbolBarkConfig);
        if (null != barker) {
            barker.shutdown();
            log.info("barker：" + symbolBarkConfig + "移除完成");
        } else {
            log.info("barker：" + symbolBarkConfig + "不存在，无法移除");
        }
    }

}
