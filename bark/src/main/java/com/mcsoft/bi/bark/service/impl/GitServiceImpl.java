package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.config.BarkGitConfig;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.service.GitService;
import com.mcsoft.bi.common.util.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Service
public class GitServiceImpl implements GitService {

    @Autowired
    private BarkGitConfig barkGitConfig;

    private final PullCommand barkPullCommand;
    private final PushCommand barkPushCommand;
    private final AddCommand barkAddCommand;
    private final CommitCommand barkCommitCommand;

    private File barkConfigFile;

    public GitServiceImpl(PullCommand barkPullCommand, PushCommand barkPushCommand, AddCommand barkAddCommand, CommitCommand barkCommitCommand) {
        this.barkPullCommand = barkPullCommand;
        this.barkPushCommand = barkPushCommand;
        this.barkAddCommand = barkAddCommand;
        this.barkCommitCommand = barkCommitCommand;
    }

    @PostConstruct
    public void init() throws Exception {
        this.barkConfigFile = new File(barkGitConfig.getBarkConfigFilePath());
        if (!barkConfigFile.exists()) {
            throw new Exception("无法获取项目git配置文件，请检查git是否拉取成功");
        }
    }

    @Override
    public BarkConfigs pullConfig() throws GitAPIException, IOException {
        barkPullCommand.call();
        final String config = FileUtils.readFileToString(barkConfigFile, StandardCharsets.UTF_8);
        return JsonUtil.readValue(config, BarkConfigs.class);
    }

    @Override
    public void pushConfig(BarkConfigs configs) throws GitAPIException, IOException {
        final String configJson = JsonUtil.writeToJson(configs);
        FileUtils.writeStringToFile(barkConfigFile, configJson, StandardCharsets.UTF_8);
        barkCommitCommand.setMessage("update config").call();
        barkPushCommand.call();
    }

}
