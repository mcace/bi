package com.mcsoft.bi.bark.config;

import com.mcsoft.bi.bark.context.AppContext;
import com.mcsoft.bi.common.git.GitSupport;
import lombok.Data;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Configuration
@ConfigurationProperties(prefix = "bi.git.bark")
@Data
public class BarkGitConfig {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(BarkGitConfig.class);

    private String repoName;
    private String barkHost;
    private String barkConfigFileName;
    private String repoPath;
    @Value("${bi.git.bark.lock}")
    private String lock;
    @Value("${bi.git.bark.time}")
    private String time;
    private String barkConfigFilePath;

    @PostConstruct
    public void init() throws GitAPIException, IOException {
        this.barkConfigFilePath = this.repoPath + File.separator + repoName + File.separator + barkConfigFileName;
        // 启动时强制与远程分支同步
        gitSupport().newAddCommand().call();
        barkGit().reset().setMode(ResetCommand.ResetType.HARD).call();
        barkGit().pull().setCredentialsProvider(provider()).call();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("系统退出，准备保存BarkConfigs");
                AppContext.currentContext().saveBarkConfigs();
            } catch (GitAPIException | IOException e) {
                log.error("退出时保存配置错误", e);
            }
        }));
    }

    @Bean
    public CredentialsProvider provider() {
        return new UsernamePasswordCredentialsProvider(lock, time);
    }

    @Bean
    public Git barkGit() throws GitAPIException, IOException {
        String realRepoPath = repoPath + File.separator + repoName;
        File realRepoFile = new File(realRepoPath);

        try {
            return Git.open(realRepoFile);
        } catch (RepositoryNotFoundException e) {
            if (realRepoFile.exists() && !realRepoFile.delete()) {
                log.error("init bark git config failed, repo file not exists and could not be able to clear directory");
                System.exit(0);
                return null;
            } else {
                return Git.cloneRepository().setURI(barkHost).setDirectory(realRepoFile).setCredentialsProvider(provider()).call();
            }
        }
    }

    @Bean
    public GitSupport gitSupport() throws GitAPIException, IOException {
        return new GitSupport(barkGit(), provider());
    }

}
