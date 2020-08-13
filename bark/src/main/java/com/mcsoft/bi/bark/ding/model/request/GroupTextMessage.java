package com.mcsoft.bi.bark.ding.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MC on 2020/8/11.
 *
 * @author MC
 */
@Data
public class GroupTextMessage {

    private String msgtype = "text";

    private Text text;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Text {
        private String content;
    }

    private At at;

    @Data
    public static class At {
        private List<String> atMobiles;
        private Boolean isAtAll = Boolean.FALSE;
    }

    public static GroupTextMessageBuilder builder() {
        return new GroupTextMessageBuilder();
    }

    public static class GroupTextMessageBuilder {
        private String content;
        private Boolean isAtAll = Boolean.FALSE;
        private List<String> atMobiles = new ArrayList<>();

        public GroupTextMessageBuilder content(String content) {
            this.content = content;
            return this;
        }

        public GroupTextMessageBuilder atAll() {
            this.isAtAll = Boolean.TRUE;
            return this;
        }

        public GroupTextMessageBuilder notAtAll() {
            this.isAtAll = Boolean.FALSE;
            return this;
        }

        public GroupTextMessageBuilder at(String phone) {
            this.atMobiles.add(phone);
            return this;
        }

        public GroupTextMessage build() {
            GroupTextMessage message = new GroupTextMessage();
            Text text = new Text(this.content);
            message.setText(text);
            At at = new At();
            at.setIsAtAll(this.isAtAll);
            at.setAtMobiles(atMobiles);
            message.setAt(at);
            return message;
        }

    }

}
