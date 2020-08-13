package com.mcsoft.bi.bark.service.impl;

import com.mcsoft.bi.bark.ding.api.DingBotApi;
import com.mcsoft.bi.bark.ding.model.request.GroupTextMessage;
import com.mcsoft.bi.bark.service.DingBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Service
public class DingBotServiceImpl implements DingBotService {

    @Autowired
    private DingBotApi dingBotApi;

    @Override
    public void sendMessageToBiGroup(String message) {
        final GroupTextMessage messageParam = GroupTextMessage.builder().content(message).build();
        dingBotApi.sendToBi(messageParam);
    }

}
