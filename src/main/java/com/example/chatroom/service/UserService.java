package com.example.chatroom.service;

import com.example.chatroom.entity.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserService {
    private static final ConcurrentHashMap<String, User> onlineUsers = new ConcurrentHashMap<>();

    public boolean login(String username, String sessionId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        username = username.trim();

        // 检查用户名是否已存在
        if (onlineUsers.containsKey(username)) {
            return false;
        }

        // 添加用户到在线列表
        User user = new User(username, sessionId);
        onlineUsers.put(username, user);
        return true;
    }

    public void updateUserActivity(String username) {
        User user = onlineUsers.get(username);
        if (user != null) {
            // 调用User的updateActivityTime方法
            user.updateActivityTime();
        }
    }

    public int cleanupInactiveUsers() {
        List<String> toRemove = new ArrayList<>();
        LocalDateTime cutoffTime = LocalDateTime.now().minusSeconds(8);

        for (ConcurrentHashMap.Entry<String, User> entry : onlineUsers.entrySet()) {
            User user = entry.getValue();
            if (user.getLastActivityTime() == null ||
                    user.getLastActivityTime().isBefore(cutoffTime)) {
                toRemove.add(entry.getKey());
            }
        }

        for (String username : toRemove) {
            logout(username);
            System.out.println("心跳检查：用户 " + username + " 超过8秒无活动，已清理");
        }

        return toRemove.size();
    }

    public boolean logout(String username) {
        if (username == null) {
            return false;
        }
        return onlineUsers.remove(username) != null;
    }

    public boolean logoutBySessionId(String sessionId) {
        for (User user : onlineUsers.values()) {
            if (user.getSessionId() != null && user.getSessionId().equals(sessionId)) {
                return logout(user.getUsername());
            }
        }
        return false;
    }

    public List<String> getOnlineUsernames() {
        return new ArrayList<>(onlineUsers.keySet());
    }

    public boolean isUsernameExists(String username) {
        return onlineUsers.containsKey(username);
    }

    public int getOnlineCount() {
        return onlineUsers.size();
    }

    public void clearAllUsers() {
        onlineUsers.clear();
    }
}