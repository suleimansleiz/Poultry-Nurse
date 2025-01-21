package com.example.pddc.ui.classes;

public class Message {
        private final String content;
        private final boolean isUser;

        public Message(String content, boolean isUser) {
            this.content = content;
            this.isUser = isUser;
        }

        public String getContent() {
            return content;
        }

        public boolean isUser() {
            return isUser;
        }
}
