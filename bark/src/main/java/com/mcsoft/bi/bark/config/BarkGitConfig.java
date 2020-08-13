package com.mcsoft.bi.bark.config;

import lombok.Data;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
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

    private String repoName;
    private String barkHost;
    private String barkConfigFileName;
    private String repoPath;
    private String username;
    private String password;
    private String barkConfigFilePath;

    @PostConstruct
    public void init() {
        this.barkConfigFilePath = this.repoPath + File.separator + repoName + File.separator + barkConfigFileName;
    }

    @Bean
    public Git barkGit() throws GitAPIException, IOException {
        String realRepoPath = repoPath + File.separator + repoName;
        File realRepoFile = new File(realRepoPath);

        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
        try {
            return Git.open(realRepoFile);
        } catch (RepositoryNotFoundException e) {
            return Git.cloneRepository().setURI(barkHost).setDirectory(new File(realRepoPath)).setCredentialsProvider(provider).call();
        }
    }

    @Bean
    public PullCommand barkPullCommand() throws GitAPIException, IOException {
        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
        final PullCommand pullCommand = barkGit().pull().setCredentialsProvider(provider);
        pullCommand.call();
        return pullCommand;
    }

    @Bean
    public PushCommand barkPushCommand() throws GitAPIException, IOException {
        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
        final PushCommand pushCommand = barkGit().push().setCredentialsProvider(provider);
        pushCommand.call();
        return pushCommand;
    }

    @Bean
    public AddCommand barkAddCommand() throws GitAPIException, IOException {
        final AddCommand addCommand = barkGit().add().addFilepattern(".");
        addCommand.call();
        return addCommand;
    }

    @Bean
    public CommitCommand barkCommitCommand() throws GitAPIException, IOException {
        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
        final CommitCommand commitCommand = barkGit().commit();
        commitCommand.setCredentialsProvider(provider);
        return commitCommand;
    }

}
