package com.mcsoft.bi.bark.service;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
public interface DingBotService {

    /**
     * 发送消息到'币'群组
     * @param message 消息
     */
    public void sendMessageToBiGroup(String message);

}
