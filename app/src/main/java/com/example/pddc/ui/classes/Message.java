package com.example.pddc.ui.classes;

public class Message {
        private final String content;
        private final boolean isUser;

        private final String time;


    public Message(String content, boolean isUser, String time ) {
            this.content = content;
            this.isUser = isUser;
            this.time = time;

    }

        public String getContent() {
            return content;
        }

        public boolean isUser() {
            return isUser;
        }

        public String getTime() {
            return time;
        }
}
