package com.mcsoft.bi.bark.context;

import com.mcsoft.bi.bark.barker.Barker;
import com.mcsoft.bi.bark.config.AppConfig;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.bark.service.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
public class AppContext {

    private static AppContext INSTANCE;

    private final GitService gitService;
    private final BarkConfigs barkConfigs;
    private final AppConfig appConfig;
    private final ConcurrentHashMap<SymbolBarkConfig, Barker> barkerMap = new ConcurrentHashMap<>(16);

    private AppContext(GitService gitService, BarkConfigs barkConfigs, AppConfig appConfig) {
        this.gitService = gitService;
        this.barkConfigs = barkConfigs;
        this.appConfig = appConfig;
    }

    public static AppContext currentContext() {
        return INSTANCE;
    }

    public BarkConfigs getBarkConfigs() {
        return barkConfigs;
    }

    public void saveBarkConfigs() throws GitAPIException, IOException {
        gitService.pushConfig(barkConfigs);
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

    public static class AppContextInit {
        public static void init(GitService gitService, AppConfig appConfig) throws GitAPIException, IOException {
            INSTANCE = new AppContext(gitService, gitService.pullConfig(), appConfig);
        }
    }

}
