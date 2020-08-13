package com.mcsoft.bi.bark.service;

import com.mcsoft.bi.bark.BarkApplication;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;
import com.mcsoft.bi.common.bian.constants.TokenConstants;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BarkApplication.class)
class GitServiceTest {

    @Autowired
    private GitService gitService;

    @Test
    void pullConfig() throws GitAPIException, IOException {
        final BarkConfigs barkConfigs = gitService.pullConfig();
        System.out.println(barkConfigs);
    }

    @Test
    void pushConfig() throws GitAPIException, IOException {
        Set<SymbolBarkConfig> symbolBarkConfigs = new HashSet<>();
        BarkConfigs barkConfigs = new BarkConfigs();
        SymbolBarkConfig config = new SymbolBarkConfig();
        config.setSymbol(TokenConstants.BTC_USDT);
        config.setSeconds(30);
        config.setPercent(new BigDecimal("0.6"));
        symbolBarkConfigs.add(config);
        barkConfigs.setSymbolBarkConfigs(symbolBarkConfigs);

        gitService.pushConfig(barkConfigs);
    }
}