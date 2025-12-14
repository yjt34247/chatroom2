package com.example.chatroom.controller;

import com.example.chatroom.entity.Message;
import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.UserService;  // 1. 导入UserService
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/message")
public class MessageController extends HttpServlet {
    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();  // 2. 添加UserService实例

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.getWriter().write("<div>请先登录</div>");
            return;
        }

        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.getWriter().write("<div>请先登录</div>");
            return;
        }

        // 3. 关键：更新用户活动时间
        userService.updateUserActivity(username);
        System.out.println("获取消息：更新用户活动时间 - " + username);

        String messagesHtml = messageService.getFormattedMessagesForUser(username);
        response.getWriter().write(messagesHtml);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // 4. 关键：更新用户活动时间
        userService.updateUserActivity(username);
        System.out.println("发送消息：更新用户活动时间 - " + username);

        String receiver = request.getParameter("receiver");
        String content = request.getParameter("message");

        if (content == null || content.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/chat.jsp");
            return;
        }

        content = content.trim();

        if (receiver == null || receiver.isEmpty()) {
            receiver = "all";
        }

        Message message = new Message(username, receiver, content);
        messageService.saveMessage(message);

        System.out.println("消息保存: " + username + " -> " + receiver + ": " + content);

        response.sendRedirect(request.getContextPath() + "/chat.jsp");
    }
}