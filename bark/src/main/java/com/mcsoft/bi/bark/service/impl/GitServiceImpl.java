package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.config.BarkGitConfig;
import com.mcsoft.bi.bark.git.GitSupport;
import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import com.mcsoft.bi.bark.service.GitService;
import com.mcsoft.bi.common.util.JsonUtil;
import org.apache.commons.io.FileUtils;
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

    private final GitSupport gitSupport;

    private File barkConfigFile;

    public GitServiceImpl(GitSupport gitSupport) {
        this.gitSupport = gitSupport;
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
        gitSupport.newPullCommand().call();
        final String config = FileUtils.readFileToString(barkConfigFile, StandardCharsets.UTF_8);
        return JsonUtil.readValue(config, BarkConfigs.class);
    }

    @Override
    public void pushConfig(BarkConfigs configs) throws GitAPIException, IOException {
        final String configJson = JsonUtil.writeToJson(configs);
        FileUtils.writeStringToFile(barkConfigFile, configJson, StandardCharsets.UTF_8);
        gitSupport.newAddCommand().call();
        gitSupport.newCommitCommand().setMessage("update config").call();
        gitSupport.newPushCommand().call();
    }

}
