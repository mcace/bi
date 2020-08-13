package com.mcsoft.bi.bark.ding.api;

import com.mcsoft.bi.bark.ding.model.request.GroupTextMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@FeignClient(name = "ding-talk", url = "https://oapi.dingtalk.com")
public interface DingBotApi {

    /**
     * 发送消息到群"币"
     *
     * @param message 消息对象
     * @return 返回数据
     */
    @PostMapping("/robot/send?access_token=46d62ea8c7ddd905dc0d398e9e05ea650ade8eb8a2fee69ce53f85b2c16efd3a")
    public String sendToBi(GroupTextMessage message);

}
