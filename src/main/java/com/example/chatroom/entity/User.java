package com.example.chatroom.entity;

import java.time.LocalDateTime;

public class User {
    private String username;
    private LocalDateTime loginTime;
    private String sessionId;
    private LocalDateTime lastActivityTime;

    // 构造方法
    public User(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
        this.loginTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getLastActivityTime() { return lastActivityTime; }
    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    // 更新活动时间
    public void updateActivityTime() {
        this.lastActivityTime = LocalDateTime.now();
    }
}