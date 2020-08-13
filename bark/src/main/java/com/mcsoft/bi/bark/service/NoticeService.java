package com.mcsoft.bi.bark.service;

import com.mcsoft.bi.bark.model.dto.SymbolBarkConfig;

/**
 * 通知服务
 * Created by MC on 2020/8/12.
 *
 * @author MC
 */
public interface NoticeService {

    /**
     * 启动通知服务
     */
    public void start();

    /**
     * 通过给定的通知服务配置启动新通知服务
     *
     * @param symbolBarkConfig 给定通知服务配置
     */
    public void startNewBark(SymbolBarkConfig symbolBarkConfig);

}
