package com.mcsoft.bi.bark.config;

import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.bark.service.GitService;
import lombok.Data;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
@Configuration
@ConfigurationProperties(prefix = "bi.bark")
@Data
public class AppConfig implements ApplicationContextAware {

    @Autowired
    private GitService gitService;
    private ApplicationContext context;

    private Long duration;

    @PostConstruct
    public void init() throws GitAPIException, IOException {
        AppContext.AppContextInit.init(gitService, this, context);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
