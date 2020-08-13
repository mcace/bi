package com.mcsoft.bi.bark.context;

import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.service.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
public class AppContext {

    private static AppContext INSTANCE;

    private GitService gitService;
    private BarkConfigs barkConfigs;

    private AppContext(GitService gitService, BarkConfigs barkConfigs) {
        this.gitService = gitService;
        this.barkConfigs = barkConfigs;
    }

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public BarkConfigs getBarkConfigs() {
        return barkConfigs;
    }

    public void saveBarkConfigs() throws GitAPIException, IOException {
        gitService.pushConfig(barkConfigs);
    }

    public static class AppContextInit {
        public static void init(GitService gitService) throws GitAPIException, IOException {
            INSTANCE = new AppContext(gitService, gitService.pullConfig());
        }
    }

}
