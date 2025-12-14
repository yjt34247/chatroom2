package com.example.chatroom.controller;

import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private final UserService userService = new UserService();
    private final MessageService messageService = new MessageService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");

        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=empty");
            return;
        }

        username = username.trim();

        // 检查用户名是否已存在
        if (userService.isUsernameExists(username)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=exists");
            return;
        }

        // 创建或获取会话
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();

        // 登录用户
        boolean loginSuccess = userService.login(username, sessionId);
        if (!loginSuccess) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=system");
            return;
        }

        // 保存用户名到会话
        session.setAttribute("username", username);
        // 设置Session 20秒无操作就过期
        session.setMaxInactiveInterval(20);

        // 添加用户加入的系统消息
        messageService.addSystemMessage(username + " 加入了聊天室");

        System.out.println("用户 " + username + " 登录成功，sessionId: " + sessionId + "，超时时间：20秒");

        response.sendRedirect(request.getContextPath() + "/chat.jsp");
    }
}