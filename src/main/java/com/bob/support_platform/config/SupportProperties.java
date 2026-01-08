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
    private Web web = new Web();

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
        private boolean greetingEnabled = true;
        private boolean headerEnabled = true;
        private boolean footerEnabled = true;
    }

    @Getter
    @Setter
    public static class Web {
        private boolean enabled = true;
        private Admin admin = new Admin();

        @Getter
        @Setter
        public static class Admin {
            private String username = "admin";
            private String password = "admin";
        }
    }
}

