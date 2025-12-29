package com.bob.support_platform.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "support")
public class SupportProperties {

    private Tickets tickets = new Tickets();
    private Messages messages = new Messages();

    //fallback значения чтобы не было NPE
    @Getter
    @Setter
    public static class Tickets {
        private boolean autoCloseOnReply = false;
    }

    @Getter
    @Setter
    public static class Messages {
        private int maxLength = 2000;
        private int limitTime = 10;
        private int limitAmountmsg = 10;
        private int limitRestrictTime = 10;
    }
}

