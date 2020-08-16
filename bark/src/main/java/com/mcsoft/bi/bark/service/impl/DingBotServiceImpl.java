package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.ding.api.DingBotApi;
import com.mcsoft.bi.bark.ding.model.request.GroupTextMessage;
import com.mcsoft.bi.bark.service.DingBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Service
public class DingBotServiceImpl implements DingBotService {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(DingBotServiceImpl.class);

    @Autowired
    private DingBotApi dingBotApi;

    @Override
    public void sendMessageToBiGroup(String message) {
        // 机器人设置内容中必须包含"提示"二字
        final GroupTextMessage messageParam = GroupTextMessage.builder().content("提示:" + message).build();
        log.debug("发送消息至钉钉：" + messageParam);
        String response = dingBotApi.sendToBi(messageParam);
        log.debug("钉钉回复：" + response);
    }

}
