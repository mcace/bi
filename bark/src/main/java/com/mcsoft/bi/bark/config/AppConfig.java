package com.mcsoft.bi.bark.config;

import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.bark.service.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
@Configuration
public class AppConfig {

    @Autowired
    private GitService gitService;

    @PostConstruct
    public void init() throws GitAPIException, IOException {
        AppContext.AppContextInit.init(gitService);
    }

}
