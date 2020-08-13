package com.mcsoft.bi.bark.service;

import com.mcsoft.bi.bark.model.dto.BarkConfigs;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * GitService，上传或拉取配置项
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
public interface GitService {

    public BarkConfigs pullConfig() throws GitAPIException, IOException;

    public void pushConfig(BarkConfigs configs) throws GitAPIException, IOException;

}
