package com.bob.support_platform.web;

import lombok.Data;
import java.util.Map;

@Data
public class SupportConfigDto {

    private Spring spring;
    private Server server;
    private Support support;

    @Data
    public static class Spring {
        private Application application;
        private Datasource datasource;
        private Jpa jpa;

        @Data
        public static class Application {
            private String name;
        }

        @Data
        public static class Datasource {
            private String username;
            private String password;
            private String url;
            private String driverClassName;
        }

        @Data
        public static class Jpa {
            private Hibernate hibernate;
            private boolean openInView;
            private boolean showSql;

            @Data
            public static class Hibernate {
                private String ddlAuto;
            }
        }
    }

    @Data
    public static class Server {
        private int port;
    }

    @Data
    public static class Support {
        private Web web;
        private Tickets tickets;
        private Messages messages;
        private Platforms platforms;
        private Map<String, String> texts;

        @Data
        public static class Web {
            private boolean enabled;
            private Admin admin;

            @Data
            public static class Admin {
                private String username;
                private String password;
            }
        }

        @Data
        public static class Tickets {
            private boolean autoCloseOnReply;
        }

        @Data
        public static class Messages {
            private int maxLength;
            private int limitTime;
            private int limitAmountmsg;
            private int limitRestrictTime;
            private boolean greetingEnabled;
            private boolean headerEnabled;
            private boolean footerEnabled;
        }

        @Data
        public static class Platforms {
            private Platform telegram;
            private Platform discord;

            @Data
            public static class Platform {
                private boolean enabled;
                private String token;
                private long supportChatId;
            }
        }
    }
}

