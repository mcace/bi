package com.mcsoft.bi.bark.context;

import com.mcsoft.bi.bark.barker.Barker;
import com.mcsoft.bi.bark.config.AppConfig;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.GitService;
import com.mcsoft.bi.bark.service.NoticeService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
public class AppContext {

    private static AppContext INSTANCE;

    private final NoticeService noticeService;
    private final GitService gitService;
    private final BarkConfigs barkConfigs;
    private final AppConfig appConfig;
    private final ConcurrentHashMap<SymbolBarkConfig, Barker> barkerMap = new ConcurrentHashMap<>(16);
    private final ApplicationContext context;
    private final Lock noticeLock = new ReentrantLock();
    private volatile State noticeState = State.NEW;

    private AppContext(NoticeService noticeService, GitService gitService, BarkConfigs barkConfigs, AppConfig appConfig, ApplicationContext context) {
        this.noticeService = noticeService;
        this.gitService = gitService;
        this.barkConfigs = barkConfigs;
        this.appConfig = appConfig;
        this.context = context;
    }

    public static AppContext currentContext() {
        return INSTANCE;
    }

    public void startNotice() {
        if (noticeState.equals(State.NEW)) {
            try {
                noticeLock.lock();
                if (noticeState.equals(State.NEW)) {
                    noticeService.start();
                    noticeState = State.RUNNING;
                }
            } finally {
                noticeLock.unlock();
            }
        }
    }

    public BarkConfigs getBarkConfigs() {
        return barkConfigs;
    }

    public void saveBarkConfigs() throws GitAPIException, IOException {
        gitService.pushConfig(barkConfigs);
    }

    public void addBarkConfig(SymbolBarkConfig config) {
        this.getBarkConfigs().getSymbolBarkConfigs().add(config);
    }

    public void removeBarkConfig(SymbolBarkConfig config) {
        this.getBarkConfigs().getSymbolBarkConfigs().remove(config);
    }

    public void computeBarkerIfAbsent(SymbolBarkConfig config, Function<? super SymbolBarkConfig, ? extends Barker> mappingFunction) {
        barkerMap.computeIfAbsent(config, mappingFunction);
    }

    public Barker getBarker(SymbolBarkConfig config) {
        return barkerMap.get(config);
    }

    public Barker removeBarker(SymbolBarkConfig config) {
        return barkerMap.remove(config);
    }

    public AppConfig getAppConfig() {
        return this.appConfig;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public static class AppContextInit {
        public static void init(NoticeService noticeService, GitService gitService, AppConfig appConfig, ApplicationContext context) throws GitAPIException, IOException {
            INSTANCE = new AppContext(noticeService, gitService, gitService.pullConfig(), appConfig, context);
        }
    }

    private static enum State {
        NEW, RUNNING
    }

}
