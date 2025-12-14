package com.example.chatroom.entity;

import java.time.LocalDateTime;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;
    private boolean isSystemMessage;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isSystemMessage = false;
    }

    // 静态工厂方法创建系统消息
    public static Message createSystemMessage(String content) {
        Message msg = new Message("系统", "all", content);
        msg.setSystemMessage(true);
        return msg;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSystemMessage() { return isSystemMessage; }
    public void setSystemMessage(boolean systemMessage) { isSystemMessage = systemMessage; }
}