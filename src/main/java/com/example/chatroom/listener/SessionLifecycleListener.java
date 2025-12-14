package com.example.chatroom.listener;

import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.UserService;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SessionLifecycleListener implements HttpSessionListener {
    private final UserService userService = new UserService();
    private final MessageService messageService = new MessageService();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Session created: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String sessionId = session.getId();
        String username = (String) session.getAttribute("username");

        System.out.println("Session destroyed: " + sessionId + ", username: " + username);

        // 从在线用户列表中移除
        if (username != null) {
            boolean removed = userService.logout(username);
            if (removed) {
                // 添加用户离开的系统消息
                String leaveMessage = username + " 已离开聊天室";
                messageService.addSystemMessage(leaveMessage);
                System.out.println("用户 " + username + " 已从在线列表移除，广播消息: " + leaveMessage);
            }
        } else {
            // 尝试通过sessionId查找
            userService.logoutBySessionId(sessionId);
        }
    }
}