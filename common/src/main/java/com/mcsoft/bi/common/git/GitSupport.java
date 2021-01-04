package com.mcsoft.bi.common.git;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.transport.CredentialsProvider;

/**
 * Git工具
 * Created by MC on 2020/8/17.
 *
 * @author MC
 */
public class GitSupport {

    private final Git git;
    private final CredentialsProvider provider;

    public GitSupport(Git git, CredentialsProvider provider) {
        this.git = git;
        this.provider = provider;
    }

    public PullCommand newPullCommand() {
        return git.pull().setCredentialsProvider(provider);
    }

    public PushCommand newPushCommand() {
        return git.push().setCredentialsProvider(provider);
    }

    public AddCommand newAddCommand() {
        return git.add().addFilepattern(".");
    }

    public CommitCommand newCommitCommand() {
        final CommitCommand commitCommand = git.commit();
        commitCommand.setCredentialsProvider(provider);
        return commitCommand;
    }

}
